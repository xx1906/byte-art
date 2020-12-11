package main

import (
	_ "github.com/go-sql-driver/mysql"
	"github.com/jinzhu/gorm"
	"github.com/sirupsen/logrus"
	"math/rand"
	"time"
)

var db *gorm.DB

const dsn = "bytebyte:123456@tcp(127.0.0.1:3306)/bytebyte?charset=utf8mb4" // 数据源
const charSequence = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
const sz = 1024 * 64

type Account struct {
	Id       uint64 `json:"id"`
	NickName string `json:"nick_name" gorm:"index"`
	Pwd      string `json:"pwd"`
}

func init() {
	var err error
	db, err = gorm.Open("mysql", dsn)
	if err != nil {
		logrus.Fatalf("open db dsn%s %s", dsn, err)
	}
	db.LogMode(true)
	db.AutoMigrate(&Account{})
}

func RanStr(ln int) string {
	rand.Seed(time.Now().UnixNano())
	buff := make([]byte, 0, ln)
	_s := []byte(charSequence)
	for i := 0; i < ln; i++ {
		buff = append(buff, (_s)[rand.Int()%len(_s)])
	}
	return string(buff)
}

func BuildAccount() *Account {
	return &Account{NickName: RanStr(10), Pwd: RanStr(16)}
}
func main() {
	for i := 0; i < sz; i++ {
		err := db.Create(BuildAccount()).Error
		logrus.Infof("build %d error:%v\n", i, err)
	}
}
