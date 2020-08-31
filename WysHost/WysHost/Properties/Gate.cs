using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Sockets;
using System.IO;
using WysHost;
using System.Runtime.InteropServices;
using System.Windows.Forms;

namespace ClientDemo0
{
    class Gate
    {
        public static bool flag = false;
        //"10.0.0.9" - example of ip
        public static int sendAndReciveFromDal(string targetIP, byte[] content, byte[] recive)
        {
            TcpClient tcpclnt = new TcpClient();
            Console.WriteLine("Connecting.....");

            tcpclnt.Connect(targetIP, 8080);
            // use the ipaddress as in the server program

            Console.WriteLine("Connected");
            Stream stm = tcpclnt.GetStream();

            byte[] send = new byte[content.Length + 1];
            send[0] = (byte)1;
            for (int i = 0; i < content.Length; i++)
            {
                send[i + 1] = content[i];
            }
            stm.Write(send, 0, send.Length);
            int size = stm.Read(recive, 0, 100);

            tcpclnt.Close();
            return size;

        }
        public static int sendAndReciveFromHost(string targetIP, byte[] content, byte[] recive)
        {
            TcpClient tcpclnt = new TcpClient();
            Console.WriteLine("Connecting.....");

            tcpclnt.Connect(targetIP, 8080);
            // use the ipaddress as in the server program

            Console.WriteLine("Connected");
            Stream stm = tcpclnt.GetStream();

            byte[] send = new byte[content.Length + 1];
            send[0] = (byte)0;
            for (int i = 0; i < content.Length; i++)
            {
                send[i + 1] = content[i];
            }
            stm.Write(send, 0, send.Length);
            int size = stm.Read(recive, 0, 100);

            tcpclnt.Close();
            return size;

        }
        public static void checkAndDoFromServer(byte[] arr)
        {
            if (arr == null)
                throw new Exception("NULL_FROM_SERVER");
            switch ((int)arr[0])
            {
                case 0:
                    //To Host
                    break;
                case 1:
                    checkToDal(lessFirstByte((arr), arr.Length));
                    //To Dal
                    break;
                default:
                    //Do nothing
                    break;
            }
        }

        public static byte[] lessFirstByte(byte[] arr, int size)
        {
            --size;
            byte[] result = new byte[size];
            for (int i = 0; i < size; i++)
            {
                result[i] = arr[i + 1];
            }
            return result;
        }

        public static void checkToDal(byte[] arr)
        {
            switch ((int)arr[0])
            {
                case 0:
                    if ((int)arr[1] == 1)
                    {
                        flag = true;
                        MessageBox.Show("please chose");
                        sendToDal(10);
                    }
                    else if ((int)arr[1] == 0)
                    {
                        MessageBox.Show("id isnt corect");
                        sendToDal(11);
                    }
                    break;
                case 1:
                    if ((int)arr[1] == 0)
                    {
                        MessageBox.Show("chosing error");
                        sendToDal(12);
                    }
                    else if ((int)arr[1] == 1)
                    {
                        MessageBox.Show("you chose good job");
                        sendToDal(13);
                        flag = false;
                    }
                    break;
                case 2:
                    MessageBox.Show("error");
                    sendToDal(14);
                    break;
                case 6:
                    MessageBox.Show("error");
                    sendToDal(14);
                    break;
                default:
                    MessageBox.Show("error");
                    sendToDal(14);
                    break;
            }
        }
        public static void sendToDal(int cmd)
        {
            const int otpLength = 10;
            byte[] otpBytes = new byte[otpLength];
            IntPtr outArr = Marshal.AllocHGlobal(otpLength);
            WysWrapper.Sendrequestfordal(outArr, otpLength, cmd);
        }
    }
}