package bitmap

import "fmt"

type Bitmap struct {
	data []byte
	size uint64
}

func NewBitmap(sz uint64) *Bitmap {
	if sz+7 < sz {
		panic(fmt.Sprintf("sz:%d overflow", sz))
	}
	sz = (sz + 7) / 8 * 8
	return &Bitmap{
		data: make([]byte, int(sz/8), int(sz/8)),
		size: uint64(sz),
	}
}

func (bm *Bitmap) Size() uint64 {
	return bm.size
}

func (bm *Bitmap) IsSet(pos uint64) bool {
	if pos >= bm.size {
		return false
	}
	if bm.data[pos>>3]&(1<<(pos&0x7)) > 0 {
		return true
	}
	return false
}

func (bm *Bitmap) Set(pos uint64) {
	if pos >= bm.size {
		return
	}
	bm.data[pos>>3] |= 1 << (pos & 0x7)
}

func (bm *Bitmap) UnSet(pos uint64) bool {
	if pos >= bm.size {
		return false
	}
	bm.data[pos>>3] ^= (1 << (pos & 0x7))
	return true
}

func (bm *Bitmap) Resize(newSz uint64) {
	if newSz+7 < newSz {
		panic(fmt.Sprintf("newSz %d overflow", newSz))
	}
	newSz = (newSz + 7) / 8 * 8
	// 等量扩容
	if newSz == bm.size {
		return
	}
	data := make([]byte, newSz/8, newSz/8)
	copy(data, bm.data)
	bm.data = data
	bm.size = newSz
}

func (bm *Bitmap) Clear() {
	bm.data = make([]byte, 0, 0)
	bm.size = 0
}
