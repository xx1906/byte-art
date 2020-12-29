# protocol buffer 编码原理

> protocol buffer 编码的原理分析
> 参考文档地址: https://developers.google.com/protocol-buffers/docs/encoding



### 基本

一个 protocol buffer 的 message 由一些列的 key-value(键值对) 对组成。message 的二进制信息只使用编号作为建， 每个字段的名称和声明的类型只能在解码结束时通过 message 类型的定义来确定(.proto文件)。



对一个 message 编码时， 键和值被串联到一个字节流中(有结构的 message 编码之后编程了无结构的字节流对象)。 对一个 message 解码时， 解析器需要能够跳过它自己不能识别的字段。通过这种方式， 可以将新字段添加到 message 中， 二不会破甲不知道它们的旧程序。为此， wire 格式 message 中每个对的 "keys" 实际上是两个值 来自于 proto 文件的字段编号， 再加上一个 wire 类型， 该类型提供了足够的信息来查找值得长度。在大多数语言实现中， 键被称为 tag。

**wire format 类型:**

| Type | Meaning          | Used For                                                 |
| ---- | ---------------- | -------------------------------------------------------- |
| 0    | Varint           | int32, int64, uint32, uint64, sint32, sint64, bool, enum |
| 1    | 64-bit           | fixed64, sfixed64, double                                |
| 2    | Length-delimited | string, bytes, embedded messages, packed repeated fields |
| 3    | Start group      | groups (deprecated)                                      |
| 4    | End group        | groups (deprecated)                                      |
| 5    | 32-bit           | fixed32, sfixed32, float                                 |

流消息中的每个键都是一个带有值的变量为: `(field_number << 3) | wire_type` (`field_number` 为message 定义字段的值, `wire_type` 为 上述表格 Type列的值)，值的最后三位存储 wire_type 类型。



流中的第一个数字总是变量的键， (使用二进制表示的话，后三位是 wire_type 类型**field_number << 3) | wire_type**使用这个公式逆推)



### 更多的类型

#### 简单的 message

```protobuf
message Test1 {
   int32 a = 1;
}
```

在应用程序中，创建一个Test1 message 并将a设置为150。然后将message 序列化为输出流。 如果您能够检查编码后的消息，则会看到三个字节：

```latex
08 96 01
```



#### 整型

所有与`wire`类型 0 相关的 protocol buffer 类型都被编码为可变变量。但是，在编码负数时，带符号的int类型（sint32和sint64）与“标准” int类型（int32和int64）之间存在重要区别。如果将int32或int64用作负数的类型，则结果varint总是十个字节长–实际上，它被视为非常大的无符号整数。如果使用带符号类型之一，则生成的varint使用ZigZag编码，效率更高。

ZigZag编码将有符号整数映射为无符号整数，以便具有较小绝对值（例如-1）的数字也具有较小的varint编码值。这样做的方式是通过正整数和负整数来回“曲折”，以便将-1编码为1，将1编码为2，将-2编码为3，依此类推， 可以在下表中看到：



| 源数值      | 编码之后数值 |
| ----------- | ------------ |
| 0           | 0            |
| -1          | 1            |
| -2          | 2            |
| 2147483647  | 4294967294   |
| -2147483648 | 4294967295   |

`sint32` 编码方式： `(n << 1) ^ (n >> 31)`

`sint64` 编码方式：`(n << 1) ^ (n >> 63)`



#### 不可变数值类型

非可变长度整数数值类型很简单– double和fixed64具有 wire 类型1，它告诉解析器期望固定的64位数据块； 同样，float和fixed32的 wire 类型为5，这告诉它期望使用32位。 在这两种情况下，值均以小端字节顺序存储。



#### 字符串

wire 类型为2(长度分隔)表示该值是 varint 编码的长度，后跟指定数量的数据字节。

```protobuf
message Test2 {
   string b = 2;
}
```

设置 `b` 的值为 `tesging` ：

```protobuf
12 07 [74 65 73 74 69 6e 67]
```

在 方括号中的字节序列是 `testing` 的 UTF-8 编码格式的字节序列：

```
0x12
→ 0001 0010  (binary representation)
→ 00010 010  (regroup bits)
→ field_number = 2, wire_type = 2
```

07 表示对应的长度值, 方括号中的 7 个字节。



#### 嵌入message

> 定义一个 message 类型 `test3` 嵌入一个 message 类型 `test1`

```protobuf
message Test1 {
   int32 a = 1;
}

message Test3 {
  optional Test1 c = 3;
}
```

这是经过编码的版本，Test1的字段同样设置为150

```latex
1a 03 08 96 01
```

正如您所看到的，最后三个字节与我们的第一个示例(08 96 01)完全相同，并且它们前面有数字3，嵌入message 的处理方式与字符串完全相同(wire type = 2)。



#### 重复类型

在proto3中，重复字段使用压缩编码。

在proto3中，默认情况下打包标量数字类型的重复字段。 这些功能类似于重复的字段，但是编码方式不同。 包含零元素的压缩重复字段不会出现在编码的 message 中。 否则，该字段的所有元素都将打包为wire 类型为2（定界）的单个键值对。 每个元素的编码方式与通常相同，不同之处在于之前没有键。



#### 字段顺序

在.proto文件中，字段号可以以任何顺序使用。所选择的顺序对message 的序列化方式没有影响。



序列化jmessage时，对于如何写入其已知字段或未知字段没有保证的顺序。 序列化顺序是一个实现细节，将来任何特定实现的细节都可能更改。 因此，protocol buffer 解析器必须能够以任何顺序解析字段。



* 不要假定序列化 message 的字节输出是稳定的。 对于具有传递性字节字段表示其他序列化protocol buffer 的 message 尤其如此。
* 默认情况下，重复调用相同protocol buffer 的 message 实例的序列化方法可能不会返回相同的字节输出;即默认的序列化是不确定的。
  * 确定性序列化只能保证特定二进制文件的字节输出相同。 字节输出可能会在二进制的不同版本之间变化。



### golang 编码🌰

[地址](../protobuffer/proto)