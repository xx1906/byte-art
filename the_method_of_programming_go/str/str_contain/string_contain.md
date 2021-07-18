# 字符串包含

1. 给定一个长字符串 a 和 一个短字符串 b。 请问，如何最快地判断出短字符串 b 中的所有字符都在长字符串 a 中？ 
请编写一个函数 `bool StringContain(a string, b string)` 实现所有的功能。

- 如果字符串 a 都是 "ABCD", 字符串 b 是 "BAD", 答案是 true, 因为字符 b 中的所有字母都在字符串 a 中。 或者说 b 的 a 的真子集。
- 如果字符串 a 是 "ABCD", 字符串 b 是 "BCE", 答案是 false. 因为 字符串 b 中的字母 E 不在字符串 a 中。
- 如果字符串 a 是 "ABCD", 字符串 b 是 "AA", 答案是 true，因为字符串 b 中的字母 A 包含在字符串 a 中。




## 审题
- 由第一个点得知： 字符串 a 中可以包含 b 中没有的字母
- 由第二点得知:   字符串 b 中的字母必须在字符穿 a 中出现
- 由第三点得知:   字母出现多次可以按一次计算


### 解法
1. 暴力枚举每个在 a, b 中的字母
