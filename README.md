# netty-decoder-demo
netty自定义解码器处理粘包、半包问题
主要处理的协议格式为：head(定长)+body
这里我使用简单格式的协议，我们把它命名为：**TinyProtocol**
TinyProtocol协议分为 Head 和 Body 连个部分
+ Head是固定的2个字节，保持内容就是Body的长度
+ Body按照Head中存储的长度存储内容
TinyProtocol协议样例：
```
02ab
```
02是head部分，里面保持了body的长度是2
ab是body

解码器：XDecoder
业务处理器：XHandler

主要的粘包、半包处理逻辑在XDecoder类中

**程序启动步骤：**
```
1.启动NettyServer
2.启动Client
```
