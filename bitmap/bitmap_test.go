package bitmap

import "testing"

func TestBitmap_IsSet(t *testing.T) {
	bitmap := NewBitmap(20)
	bitmap.Set(3)
	bitmap.Set(2)
	bitmap.Set(1)
	bitmap.Set(0)
	t.Log(bitmap.IsSet(3))
	t.Logf("%b", bitmap.data)
}

func TestBitmap_Clear(t *testing.T) {
	bitmap := NewBitmap(20)
	bitmap.Set(3)
	bitmap.Set(2)
	bitmap.Set(1)
	bitmap.Set(0)
	t.Log(bitmap.IsSet(3))
	t.Logf("%b", bitmap.data)
	bitmap.Clear()
	t.Logf("%b", bitmap.data)
}
