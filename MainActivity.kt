package com.example.a10716.myapplication

import android.app.Application
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Xml
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.net.Socket
import java.util.stream.Stream
import java.io.*
//import sun.security.util.Length
import android.support.annotation.RequiresPermission.Read
import android.support.annotation.RequiresPermission.Write
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files.exists






class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        //sample_text.text = stringFromJNI()

        button.setOnClickListener {
            textView2.setText(textView.text.toString())
            if(textView.text.toString()!=""){
                if (!SocketClient.isConnected) {
                    startTCP()
                    if (SocketClient.isConnected) {
                        println("服务器重连成功")
                    }
                    else {
                        println("服务器重连失败")
                       //Application.Exit()

                    }

                }
                //timer4.Enabled = true
                textView2.setText("")
                newMsg = false
                println(newMsg)
                var t = 0
                var temp:String="sql- select name from student where(classnumber='" + textView.text.toString() + "');"
                println("sql- select name from student where(classnumber='" + textView.text.toString() + "');")
                Thread.sleep(500)
                ClientSendMsg("sql- select name from student where(classnumber='" + textView.text.toString() + "');")
                while (!newMsg && t < 100) {
                    Thread.sleep(50)
                    t++
                }
                if (t < 100) {
                    val a = message.split('$')
                    textView2.setText (a[0])

                }
                newMsg = false
                t = 0
                //ClientSendMsg("sql- select id from student where(classnumber='" + textView.text.toString() + "') ")//获得当前学生id
                while (!newMsg && t < 100) {
                    Thread.sleep(50)
                    t++
                }
                if (t < 100) {
                    student_id = message.split('$')[0]
                    println("student_id:$student_id\n\r")
                }
            }

        }
        textView.setOnClickListener{
            if(!textView.isInEditMode){

            }
            else{

            }
        }

    }

    var ThreadClient: Thread? = null
    var SocketClient: Socket =Socket()

    var flag_scr: Boolean = false
    var flag_cam: Boolean = false
    var flash_scr: Boolean = false
    var flash_cam: Boolean = false

    var newMsg: Boolean= false
    var message: String = ""

    var room_id: String = ""
    var room_name: String = ""
    var student_id: String = ""
    var class_id: String = ""
    var class_name: String = ""

    fun startTCP()//开启TCP链接
    {
        val mRunnable = Runnable {

            run {
                try {
                    var port: Int = 5500;
                    var host: String = "117.80.86.174";//服务器端ip地址
                    //string host2 = "127.0.0.1";//本地调试用ip


                    try {
                        //客户端套接字连接到网络节点上，用的是Connect
                        SocketClient = Socket(host, port)

                    } catch (e: Exception) {
                        Thread.sleep(1000);
                        println(e.message)



                    }


                    Thread(nRunnable).start()
                    //ThreadClient.


                    Thread.sleep(1000);


                } catch (ex: Exception) {
                    textView5.setText(ex.message)
                    throw ex;

                }
            }
        }
        Thread(mRunnable).start()
    }

    val nRunnable = Runnable {
        run {
            Recv()
        }
    }//监听线程

    fun Recv()//接收指令和数据库返回值
    {

        //持续监听服务端发来的消息
        //持续监听服务端发来的消息
        while (true) {
            try {
                //定义一个1M的内存缓冲区，用于临时性存储接收到的消息
                val arrRecvmsg :String=""

                //将客户端套接字接收到的数据存入内存缓冲区，并获取长度
                //val length = SocketClient.(arrRecvmsg)
                var o: InputStream =SocketClient.getInputStream()
                //将套接字获取到的字符数组转换为人可以看懂的字符串
                //arrRecvmsg = o.readBytes();

                var br:BufferedReader = BufferedReader( InputStreamReader(o));
                var info:String = "";
                while(!({info=br.readLine();info}).equals("")){
                    arrRecvmsg.plus(info)
                    info=""
                }
                println("rec:"+arrRecvmsg)
                if (arrRecvmsg.startsWith("f-t") || arrRecvmsg.startsWith("t-f") ||//接收到权限变化指令

                    arrRecvmsg.startsWith("f-f") || arrRecvmsg.startsWith("t-t") || arrRecvmsg.startsWith("fs") || arrRecvmsg.startsWith(
                        "fc"
                    )
                ) {
                    if (arrRecvmsg.endsWith(room_name)) {
                        val s = arrRecvmsg.replace(room_name, "$")
                        println(s)
                        when (s) {
                            "f-t$" -> {
                                flag_scr = false
                                flag_cam = true
                            }
                            "t-f$" -> {
                                flag_scr = true
                                flag_cam = false
                            }
                            "f-f$" -> {
                                flag_scr = false
                                flag_cam = false
                            }
                            "t-t$" -> {
                                flag_scr = true
                                flag_cam = true
                            }
                            "fs$" -> {
                                flash_scr = true
                            }
                            "fc$" -> {
                                flash_cam = true
                            }
                        }
                    }
                } else {
                    message = arrRecvmsg
                    newMsg = true
                    println(message + "\r\n")
                }
                br.close()
                o.close();


            } catch (ex: Exception) {

                println("远程服务器已经中断连接！" + ex + "\r\n")
                break
            }
        }
    }

    fun ClientSendMsg(sendMsg:String)//发送套接字方法
    {
        val mRunnable = Runnable {

            run {
        try
        {
            println("发送线程："+Thread.currentThread().id.toString())
            //将输入的内容字符串转换为机器可以识别的字节数组
           var arrClientSendMsg:ByteArray = sendMsg.toByteArray(Charsets.UTF_8)
            //调用客户端套接字发送字节数组
            var os:OutputStream=SocketClient.getOutputStream()
           var pw:PrintStream= PrintStream(os);
            pw.write(arrClientSendMsg)
            pw.flush()
            pw.close()
            os.close()
        }
        catch (e:Exception)
        {

            throw e
        }
            }
        }
        //将输入的内容字符串转换为机器可以识别的字节数组
        Thread(mRunnable).start()
    }

    fun sendLogin()//广播教室号登陆
    {
        ClientSendMsg("LoginStudent_" + room_id)
    }

    fun Download(url_in: String, savePath: String,fileName:String)//下载文件方法
    {
        val mRunnable = Runnable {

            run {
        var flag: Boolean = false
        val url:URL = URL(url_in)
        val connection = url.openConnection() as HttpURLConnection
        connection.setConnectTimeout(3 * 1000)
        //设置请求头
        connection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36"
        )
        //获取输入流
        val `in` = connection.getInputStream()

        val saveDir = File(savePath)
        if (!saveDir.exists()) {
            saveDir.mkdirs()
        }
        val file = File(savePath + fileName)

        val out = FileOutputStream(file)

        val bytes = ByteArray(1024)
        var len = 0
        while (!({len = `in`.read(bytes);len}).equals(-1)) {
            out.write(bytes, 0, len)
        }
        out.close()
        `in`.close()
            }
        }
        Thread(mRunnable).start()
    }



    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
