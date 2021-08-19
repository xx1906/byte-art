package main

import (
	"github.com/gin-gonic/gin"
	"github.com/sirupsen/logrus"
	"time"
)

var engine = gin.New()

func main() {
	log := logrus.New()
	engine.Use(
		gin.RecoveryWithWriter(log.Writer(), func(c *gin.Context, err interface{}) {
			log.Error(err)
		}), // recovery

		gin.LoggerWithWriter(log.Writer()), // logger
	)

	// tcp 闪断测试
	engine.GET("/tcp/flash", func(ctx *gin.Context) {
		time.Sleep(time.Minute)
		ctx.JSON(200, gin.H{"desc": ctx.Request.RequestURI})
	})

	if err := engine.Run("0.0.0.0:8080"); err != nil {
		log.Fatal("error ", err)
	}
}
