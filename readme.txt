-- 配置 配置application.properties
step.login.account=账号
step.login.password=密码
step.login.minStep=最小步数
logging.file.name=log/step.log
token.file.name=log/token

-- 打包后启动
nohup java -jar demo-step.jar &
nohup java -jar demo-step.jar >/dev/null 2>&1 &

& 是一个描述符，如果1或2前不加&，会被当成一个普通文件
1>&2 意思是把标准输出重定向到标准错误.
2>&1 意思是把标准错误输出重定向到标准输出
&>filename 意思是把标准输出和标准错误输出都重定向到文件filename中