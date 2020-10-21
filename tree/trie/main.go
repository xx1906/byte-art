package main

type Trie struct {
	data    [26]*Trie
	isWorld bool
}

/** Initialize your data structure here. */
func Constructor() Trie {
	return Trie{
		data:    [26]*Trie{},
		isWorld: false,
	}
}

const a = 'a'

/** Inserts a word into the trie. */
func (this *Trie) Insert(word string) {
	data := this
	for i := range word {
		idx := int(word[i] - a)
		if data.data[idx] == nil {
			tmp := Constructor()
			data.data[idx] = &tmp
		}
		data = data.data[idx]
	}
	data.isWorld = true
}

/** Returns if the word is in the trie. */
func (this *Trie) Search(word string) bool {
	data := this
	for i := range word {
		idx := int(word[i] - a)
		if data.data[idx] == nil {
			return false
		}
		data = data.data[idx]
	}
	return data.isWorld
}

/** Returns if there is any word in the trie that starts with the given prefix. */
func (this *Trie) StartsWith(prefix string) bool {
	data := this
	for i := range prefix {
		idx := int(prefix[i] - a)
		if data.data[idx] == nil {
			return false
		}
		data = data.data[idx]
	}
	return true
}

/**
 * Your Trie object will be instantiated and called as such:
 * obj := Constructor();
 * obj.Insert(word);
 * param_2 := obj.Search(word);
 * param_3 := obj.StartsWith(prefix);
 */

func main() {

}
