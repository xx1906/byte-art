//
//grpc 编码方式解析
//参考文档: https://developers.google.com/protocol-buffers/docs/encoding
//@author 我的我的
//重点: golang 里面 -- 如果是默认值的， 默认不编码

// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.25.0
// 	protoc        v3.14.0
// source: demo.proto

package demo

import (
	proto "github.com/golang/protobuf/proto"
	protoreflect "google.golang.org/protobuf/reflect/protoreflect"
	protoimpl "google.golang.org/protobuf/runtime/protoimpl"
	reflect "reflect"
	sync "sync"
)

const (
	// Verify that this generated code is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(20 - protoimpl.MinVersion)
	// Verify that runtime/protoimpl is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(protoimpl.MaxVersion - 20)
)

// This is a compile-time assertion that a sufficiently up-to-date version
// of the legacy proto package is being used.
const _ = proto.ProtoPackageIsVersion4

type Test1 struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	A int32           `protobuf:"varint,1,opt,name=a,proto3" json:"a,omitempty"`
	B int32           `protobuf:"zigzag32,2,opt,name=b,proto3" json:"b,omitempty"`
	C int64           `protobuf:"zigzag64,3,opt,name=c,proto3" json:"c,omitempty"` // varint int64
	D uint32          `protobuf:"fixed32,4,opt,name=d,proto3" json:"d,omitempty"`  // fixed 32 bit
	E uint64          `protobuf:"fixed64,5,opt,name=e,proto3" json:"e,omitempty"`  // fixed64 bit
	F float64         `protobuf:"fixed64,7,opt,name=f,proto3" json:"f,omitempty"`
	G float32         `protobuf:"fixed32,8,opt,name=g,proto3" json:"g,omitempty"`
	H []int32         `protobuf:"varint,9,rep,packed,name=h,proto3" json:"h,omitempty"`
	I map[int32]int32 `protobuf:"bytes,10,rep,name=i,proto3" json:"i,omitempty" protobuf_key:"varint,1,opt,name=key,proto3" protobuf_val:"varint,2,opt,name=value,proto3"` // map 类型
}

func (x *Test1) Reset() {
	*x = Test1{}
	if protoimpl.UnsafeEnabled {
		mi := &file_demo_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Test1) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Test1) ProtoMessage() {}

func (x *Test1) ProtoReflect() protoreflect.Message {
	mi := &file_demo_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Test1.ProtoReflect.Descriptor instead.
func (*Test1) Descriptor() ([]byte, []int) {
	return file_demo_proto_rawDescGZIP(), []int{0}
}

func (x *Test1) GetA() int32 {
	if x != nil {
		return x.A
	}
	return 0
}

func (x *Test1) GetB() int32 {
	if x != nil {
		return x.B
	}
	return 0
}

func (x *Test1) GetC() int64 {
	if x != nil {
		return x.C
	}
	return 0
}

func (x *Test1) GetD() uint32 {
	if x != nil {
		return x.D
	}
	return 0
}

func (x *Test1) GetE() uint64 {
	if x != nil {
		return x.E
	}
	return 0
}

func (x *Test1) GetF() float64 {
	if x != nil {
		return x.F
	}
	return 0
}

func (x *Test1) GetG() float32 {
	if x != nil {
		return x.G
	}
	return 0
}

func (x *Test1) GetH() []int32 {
	if x != nil {
		return x.H
	}
	return nil
}

func (x *Test1) GetI() map[int32]int32 {
	if x != nil {
		return x.I
	}
	return nil
}

type Test2 struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	A int32 `protobuf:"varint,1,opt,name=a,proto3" json:"a,omitempty"`
	B bool  `protobuf:"varint,2,opt,name=b,proto3" json:"b,omitempty"`
}

func (x *Test2) Reset() {
	*x = Test2{}
	if protoimpl.UnsafeEnabled {
		mi := &file_demo_proto_msgTypes[1]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Test2) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Test2) ProtoMessage() {}

func (x *Test2) ProtoReflect() protoreflect.Message {
	mi := &file_demo_proto_msgTypes[1]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Test2.ProtoReflect.Descriptor instead.
func (*Test2) Descriptor() ([]byte, []int) {
	return file_demo_proto_rawDescGZIP(), []int{1}
}

func (x *Test2) GetA() int32 {
	if x != nil {
		return x.A
	}
	return 0
}

func (x *Test2) GetB() bool {
	if x != nil {
		return x.B
	}
	return false
}

type Test3 struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	A int32 `protobuf:"varint,1,opt,name=a,proto3" json:"a,omitempty"`
	B bool  `protobuf:"varint,2,opt,name=b,proto3" json:"b,omitempty"`
	C int32 `protobuf:"zigzag32,3,opt,name=c,proto3" json:"c,omitempty"`
}

func (x *Test3) Reset() {
	*x = Test3{}
	if protoimpl.UnsafeEnabled {
		mi := &file_demo_proto_msgTypes[2]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Test3) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Test3) ProtoMessage() {}

func (x *Test3) ProtoReflect() protoreflect.Message {
	mi := &file_demo_proto_msgTypes[2]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Test3.ProtoReflect.Descriptor instead.
func (*Test3) Descriptor() ([]byte, []int) {
	return file_demo_proto_rawDescGZIP(), []int{2}
}

func (x *Test3) GetA() int32 {
	if x != nil {
		return x.A
	}
	return 0
}

func (x *Test3) GetB() bool {
	if x != nil {
		return x.B
	}
	return false
}

func (x *Test3) GetC() int32 {
	if x != nil {
		return x.C
	}
	return 0
}

type Test4 struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	A *Test1 `protobuf:"bytes,1,opt,name=a,proto3" json:"a,omitempty"`
}

func (x *Test4) Reset() {
	*x = Test4{}
	if protoimpl.UnsafeEnabled {
		mi := &file_demo_proto_msgTypes[3]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Test4) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Test4) ProtoMessage() {}

func (x *Test4) ProtoReflect() protoreflect.Message {
	mi := &file_demo_proto_msgTypes[3]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Test4.ProtoReflect.Descriptor instead.
func (*Test4) Descriptor() ([]byte, []int) {
	return file_demo_proto_rawDescGZIP(), []int{3}
}

func (x *Test4) GetA() *Test1 {
	if x != nil {
		return x.A
	}
	return nil
}

type Test5 struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	A []*Test1 `protobuf:"bytes,1,rep,name=a,proto3" json:"a,omitempty"`
}

func (x *Test5) Reset() {
	*x = Test5{}
	if protoimpl.UnsafeEnabled {
		mi := &file_demo_proto_msgTypes[4]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Test5) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Test5) ProtoMessage() {}

func (x *Test5) ProtoReflect() protoreflect.Message {
	mi := &file_demo_proto_msgTypes[4]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Test5.ProtoReflect.Descriptor instead.
func (*Test5) Descriptor() ([]byte, []int) {
	return file_demo_proto_rawDescGZIP(), []int{4}
}

func (x *Test5) GetA() []*Test1 {
	if x != nil {
		return x.A
	}
	return nil
}

type Test6 struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	A []*Test1 `protobuf:"bytes,1,rep,name=a,proto3" json:"a,omitempty"`
	B int32    `protobuf:"zigzag32,2,opt,name=b,proto3" json:"b,omitempty"`
}

func (x *Test6) Reset() {
	*x = Test6{}
	if protoimpl.UnsafeEnabled {
		mi := &file_demo_proto_msgTypes[5]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Test6) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Test6) ProtoMessage() {}

func (x *Test6) ProtoReflect() protoreflect.Message {
	mi := &file_demo_proto_msgTypes[5]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Test6.ProtoReflect.Descriptor instead.
func (*Test6) Descriptor() ([]byte, []int) {
	return file_demo_proto_rawDescGZIP(), []int{5}
}

func (x *Test6) GetA() []*Test1 {
	if x != nil {
		return x.A
	}
	return nil
}

func (x *Test6) GetB() int32 {
	if x != nil {
		return x.B
	}
	return 0
}

var File_demo_proto protoreflect.FileDescriptor

var file_demo_proto_rawDesc = []byte{
	0x0a, 0x0a, 0x64, 0x65, 0x6d, 0x6f, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x22, 0xca, 0x01, 0x0a,
	0x05, 0x54, 0x65, 0x73, 0x74, 0x31, 0x12, 0x0c, 0x0a, 0x01, 0x61, 0x18, 0x01, 0x20, 0x01, 0x28,
	0x05, 0x52, 0x01, 0x61, 0x12, 0x0c, 0x0a, 0x01, 0x62, 0x18, 0x02, 0x20, 0x01, 0x28, 0x11, 0x52,
	0x01, 0x62, 0x12, 0x0c, 0x0a, 0x01, 0x63, 0x18, 0x03, 0x20, 0x01, 0x28, 0x12, 0x52, 0x01, 0x63,
	0x12, 0x0c, 0x0a, 0x01, 0x64, 0x18, 0x04, 0x20, 0x01, 0x28, 0x07, 0x52, 0x01, 0x64, 0x12, 0x0c,
	0x0a, 0x01, 0x65, 0x18, 0x05, 0x20, 0x01, 0x28, 0x06, 0x52, 0x01, 0x65, 0x12, 0x0c, 0x0a, 0x01,
	0x66, 0x18, 0x07, 0x20, 0x01, 0x28, 0x01, 0x52, 0x01, 0x66, 0x12, 0x0c, 0x0a, 0x01, 0x67, 0x18,
	0x08, 0x20, 0x01, 0x28, 0x02, 0x52, 0x01, 0x67, 0x12, 0x0c, 0x0a, 0x01, 0x68, 0x18, 0x09, 0x20,
	0x03, 0x28, 0x05, 0x52, 0x01, 0x68, 0x12, 0x1b, 0x0a, 0x01, 0x69, 0x18, 0x0a, 0x20, 0x03, 0x28,
	0x0b, 0x32, 0x0d, 0x2e, 0x54, 0x65, 0x73, 0x74, 0x31, 0x2e, 0x49, 0x45, 0x6e, 0x74, 0x72, 0x79,
	0x52, 0x01, 0x69, 0x1a, 0x34, 0x0a, 0x06, 0x49, 0x45, 0x6e, 0x74, 0x72, 0x79, 0x12, 0x10, 0x0a,
	0x03, 0x6b, 0x65, 0x79, 0x18, 0x01, 0x20, 0x01, 0x28, 0x05, 0x52, 0x03, 0x6b, 0x65, 0x79, 0x12,
	0x14, 0x0a, 0x05, 0x76, 0x61, 0x6c, 0x75, 0x65, 0x18, 0x02, 0x20, 0x01, 0x28, 0x05, 0x52, 0x05,
	0x76, 0x61, 0x6c, 0x75, 0x65, 0x3a, 0x02, 0x38, 0x01, 0x22, 0x23, 0x0a, 0x05, 0x54, 0x65, 0x73,
	0x74, 0x32, 0x12, 0x0c, 0x0a, 0x01, 0x61, 0x18, 0x01, 0x20, 0x01, 0x28, 0x05, 0x52, 0x01, 0x61,
	0x12, 0x0c, 0x0a, 0x01, 0x62, 0x18, 0x02, 0x20, 0x01, 0x28, 0x08, 0x52, 0x01, 0x62, 0x22, 0x31,
	0x0a, 0x05, 0x54, 0x65, 0x73, 0x74, 0x33, 0x12, 0x0c, 0x0a, 0x01, 0x61, 0x18, 0x01, 0x20, 0x01,
	0x28, 0x05, 0x52, 0x01, 0x61, 0x12, 0x0c, 0x0a, 0x01, 0x62, 0x18, 0x02, 0x20, 0x01, 0x28, 0x08,
	0x52, 0x01, 0x62, 0x12, 0x0c, 0x0a, 0x01, 0x63, 0x18, 0x03, 0x20, 0x01, 0x28, 0x11, 0x52, 0x01,
	0x63, 0x22, 0x1d, 0x0a, 0x05, 0x54, 0x65, 0x73, 0x74, 0x34, 0x12, 0x14, 0x0a, 0x01, 0x61, 0x18,
	0x01, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x06, 0x2e, 0x54, 0x65, 0x73, 0x74, 0x31, 0x52, 0x01, 0x61,
	0x22, 0x1d, 0x0a, 0x05, 0x54, 0x65, 0x73, 0x74, 0x35, 0x12, 0x14, 0x0a, 0x01, 0x61, 0x18, 0x01,
	0x20, 0x03, 0x28, 0x0b, 0x32, 0x06, 0x2e, 0x54, 0x65, 0x73, 0x74, 0x31, 0x52, 0x01, 0x61, 0x22,
	0x2b, 0x0a, 0x05, 0x54, 0x65, 0x73, 0x74, 0x36, 0x12, 0x14, 0x0a, 0x01, 0x61, 0x18, 0x01, 0x20,
	0x03, 0x28, 0x0b, 0x32, 0x06, 0x2e, 0x54, 0x65, 0x73, 0x74, 0x31, 0x52, 0x01, 0x61, 0x12, 0x0c,
	0x0a, 0x01, 0x62, 0x18, 0x02, 0x20, 0x01, 0x28, 0x11, 0x52, 0x01, 0x62, 0x42, 0x06, 0x5a, 0x04,
	0x64, 0x65, 0x6d, 0x6f, 0x62, 0x06, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x33,
}

var (
	file_demo_proto_rawDescOnce sync.Once
	file_demo_proto_rawDescData = file_demo_proto_rawDesc
)

func file_demo_proto_rawDescGZIP() []byte {
	file_demo_proto_rawDescOnce.Do(func() {
		file_demo_proto_rawDescData = protoimpl.X.CompressGZIP(file_demo_proto_rawDescData)
	})
	return file_demo_proto_rawDescData
}

var file_demo_proto_msgTypes = make([]protoimpl.MessageInfo, 7)
var file_demo_proto_goTypes = []interface{}{
	(*Test1)(nil), // 0: Test1
	(*Test2)(nil), // 1: Test2
	(*Test3)(nil), // 2: Test3
	(*Test4)(nil), // 3: Test4
	(*Test5)(nil), // 4: Test5
	(*Test6)(nil), // 5: Test6
	nil,           // 6: Test1.IEntry
}
var file_demo_proto_depIdxs = []int32{
	6, // 0: Test1.i:type_name -> Test1.IEntry
	0, // 1: Test4.a:type_name -> Test1
	0, // 2: Test5.a:type_name -> Test1
	0, // 3: Test6.a:type_name -> Test1
	4, // [4:4] is the sub-list for method output_type
	4, // [4:4] is the sub-list for method input_type
	4, // [4:4] is the sub-list for extension type_name
	4, // [4:4] is the sub-list for extension extendee
	0, // [0:4] is the sub-list for field type_name
}

func init() { file_demo_proto_init() }
func file_demo_proto_init() {
	if File_demo_proto != nil {
		return
	}
	if !protoimpl.UnsafeEnabled {
		file_demo_proto_msgTypes[0].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Test1); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_demo_proto_msgTypes[1].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Test2); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_demo_proto_msgTypes[2].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Test3); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_demo_proto_msgTypes[3].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Test4); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_demo_proto_msgTypes[4].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Test5); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_demo_proto_msgTypes[5].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Test6); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
	}
	type x struct{}
	out := protoimpl.TypeBuilder{
		File: protoimpl.DescBuilder{
			GoPackagePath: reflect.TypeOf(x{}).PkgPath(),
			RawDescriptor: file_demo_proto_rawDesc,
			NumEnums:      0,
			NumMessages:   7,
			NumExtensions: 0,
			NumServices:   0,
		},
		GoTypes:           file_demo_proto_goTypes,
		DependencyIndexes: file_demo_proto_depIdxs,
		MessageInfos:      file_demo_proto_msgTypes,
	}.Build()
	File_demo_proto = out.File
	file_demo_proto_rawDesc = nil
	file_demo_proto_goTypes = nil
	file_demo_proto_depIdxs = nil
}
