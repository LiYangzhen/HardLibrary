#### HardLibrary-Backend

**导入：**
在idea中选择File->New->Project from Existing Sources.. 选择项目目录中的pom.xml文件，一路next，然后按照提示安装相关依赖。

**配置：**
首先需要配置好mysql数据库，然后在src/main/resources/application.yml文件中修改成自己数据库的账号密码。

**权限检测：**
仅部分接口不受限制，其他接口均需要登陆成功并在请求时附带合法token时才能使用，否则会返回错误状态码：

​        	未登录或token无效：403

​			登陆过期：408 （有效时长为5小时）
