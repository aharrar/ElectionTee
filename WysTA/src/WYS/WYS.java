/*++
   Copyright (c) 2010-2017 Intel Corporation. All Rights Reserved.

   The source code contained or described herein and all documents related
   to the source code ("Material") are owned by Intel Corporation or its
   suppliers or licensors. Title to the Material remains with Intel Corporation
   or its suppliers and licensors. The Material contains trade secrets and
   proprietary and confidential information of Intel or its suppliers and
   licensors. The Material is protected by worldwide copyright and trade secret
   laws and treaty provisions. No part of the Material may be used, copied,
   reproduced, modified, published, uploaded, posted, transmitted, distributed, 
   or disclosed in any way without Intel's prior express written permission.

   No license under any patent, copyright, trade secret or other intellectual
   property right is granted to or conferred upon you by disclosure or delivery
   of the Materials, either expressly, by implication, inducement, estoppel or
   otherwise. Any license under such intellectual property rights must be
   express and approved by Intel in writing.
--*/
package WYS;


import com.intel.crypto.Random;

import com.intel.crypto.RsaAlg;
import com.intel.langutil.ArrayUtils;
import com.intel.langutil.TypeConverter;
import com.intel.util.IntelApplet;
import com.intel.util.DebugPrint;
import com.intel.util.FlashStorage;

//
// Implementation of DAL Trusted Application: WYSApplet 
//
// ***************************************
// NOTE:  This Trusted Application is intended for API Level 2 and above
// ***************************************

public class WYS extends IntelApplet 
{
	static final int COMMAND_ID_CHECK_INPUT_STATUS = 1;
	static final int COMMAND_ID_GET_ID = 2;
	static final int FromServer=3;
	private StandardWindow m_standardWindow;
	
	private final byte[] PIN = {1, 7, 1, 7};
	
	/*
	 * This method will be called by the VM when a new session is opened to the Trusted Application 
	 * and this Trusted Application instance is being created to handle the new session.
	 * This method cannot provide response data and therefore calling
	 * setResponse or setResponseCode methods from it will throw a NullPointerException.
	 * 
	 * @param	request	the input data sent to the Trusted Application during session creation
	 * 
	 * @return	APPLET_SUCCESS if the operation was processed successfully, 
	 * any other error status code otherwise (note that all error codes will be
	 * treated similarly by the VM by sending "cancel" error code to the SW application).
	 */
	public int onInit(byte[] request) {
		DebugPrint.printString("WYS applet enterede election");
		byte[] help= {'0'};
		FlashStorage.writeFlashData(0, help, 0, help.length);
		m_standardWindow = StandardWindow.getInstance();

		
		return APPLET_SUCCESS;
	}
	
	/*
	 * This method will be called by the VM to handle a command sent to this
	 * Trusted Application instance.
	 * 
	 * @param	commandID	the command ID (Trusted Application specific) 
	 * @param	request		the input data for this command 
	 * @return	the return value should not be used by the applet
	 */
	public int invokeCommand(int commandID, byte[] request) {
		int res = IntelApplet.APPLET_ERROR_NOT_SUPPORTED;
		byte[] bla= {'o','k'};
		DebugPrint.printInt(commandID);

		switch (commandID)
		{
			case StandardWindow.STANDARD_COMMAND_ID:
				
				DebugPrint.printString("Processing standard command...");
				res = m_standardWindow.processCommand(commandID, request, 0);
				if ( (res == IntelApplet.APPLET_SUCCESS) &&
					 (m_standardWindow.getResponseSize() > 0) )
				{
					byte[] response = new byte[m_standardWindow.getResponseSize()];
					m_standardWindow.getResponse(response, 0);
					setResponse(response, 0, response.length);
				}
				break;
				
			case COMMAND_ID_CHECK_INPUT_STATUS:
				
				DebugPrint.printString("Checking user input status...");
				if ( isOtpAllowed() )
				{
					DebugPrint.printString("User input is OK");
					res = IntelApplet.APPLET_SUCCESS;
				}
				else
				{
					DebugPrint.printString("User input is wrong");
					res = IntelApplet.APPLET_ERROR_BAD_PARAMETERS;
				}
				break; 
			case COMMAND_ID_GET_ID:
				final byte[] flag = new byte[128];
				byte[] userPIN = m_standardWindow.getPin();
				byte[] a=new byte[userPIN.length+1];
				FlashStorage.readFlashData(0, flag, 0);
				if(flag[0]=='1') {
					ArrayUtils.copyByteArray(userPIN, 0, a, 1, userPIN.length);
					a[0]=1;
					res = IntelApplet.APPLET_SUCCESS; 
					setResponse(a, 0, a.length);
					byte[] help= {'0'};
					FlashStorage.writeFlashData(0, help, 0, help.length);
					break;
				}
				DebugPrint.printBuffer(flag);
				DebugPrint.printString("Getting ID ELECTOR...");
				DebugPrint.printBuffer(userPIN);
				DebugPrint.printString("a");
				DebugPrint.printString("-------");

				ArrayUtils.copyByteArray(userPIN, 0, a, 1, userPIN.length);
				a[0]=0;
				DebugPrint.printBuffer(a);
				DebugPrint.printString("-------");
				byte[] output=encryptwithpub(a);
				DebugPrint.printInt(output.length);
				setResponse(output, 0, output.length);
				res = IntelApplet.APPLET_SUCCESS; 
				break;
		    case 10:
					DebugPrint.printString("10");
					DebugPrint.printString("id is corect");
					byte[] help= {'1'};
					FlashStorage.writeFlashData(0, help, 0, help.length);
					setResponse(bla, 0,bla.length);

				    break;
			case 11:
					DebugPrint.printString("11");
					DebugPrint.printString("id isnt corect");
					setResponse(bla, 0,bla.length);

			        break;
				case 12:
					DebugPrint.printString("12");
					DebugPrint.printString("envlope isnt corect");
					setResponse(bla, 0,bla.length);

			        break;
				case 13:
					DebugPrint.printString("13");
					DebugPrint.printString("envlope is corect");
					
					setResponse(bla, 0,bla.length);
				case 14:
					DebugPrint.printString("14");
					DebugPrint.printString("else");
			        break;
			
			default:
				break;
		}
		
		setResponseCode(res);
		
		/*
		 * The return value of the invokeCommand method is not guaranteed to be
		 * delivered to the SW application, and therefore should not be used for
		 * this purpose. Trusted Application is expected to return APPLET_SUCCESS code 
		 * from this method and use the setResposeCode method instead.
		 */
		return APPLET_SUCCESS;
	}
	
	private int getOtp()
	{
		if ( !isOtpAllowed() )
		{
			DebugPrint.printString("OTP is blocked.");
			return IntelApplet.APPLET_ERROR_BAD_STATE;
		}
		
		byte[] otp = new byte[TypeConverter.INT_BYTE_SIZE];
		Random.getRandomBytes(otp, (short)0, (short)otp.length);
		byte[] userPIN = m_standardWindow.getPin();
		setResponse(userPIN, 0, userPIN.length);
		
		return IntelApplet.APPLET_SUCCESS;
	}

	private boolean isOtpAllowed()
	{
		if ( m_standardWindow.getUserInputStatus() == true )
		{
			return true;
		}
		// try to check PIN number
		byte[] userPIN = m_standardWindow.getPin();
		DebugPrint.printString("user pin");

		DebugPrint.printBuffer(userPIN);

		if ( (userPIN != null) && (userPIN.length == 8) )
		{
			
			return true;
		}
		
		return false;
	}
	
	/*
	 * This method will be called by the VM when the session being handled by
	 * this Trusted Application instance is being closed 
	 * and this Trusted Application instance is about to be removed.
	 * This method cannot provide response data and therefore
	 * calling setResponse or setResponseCode methods from it will throw a NullPointerException.
	 * 
	 * @return APPLET_SUCCESS code (the status code is not used by the VM).
	 */
	public int onClose() {
		DebugPrint.printString("WYS applet exited");
		return APPLET_SUCCESS;
	}
	
	public byte[] encryptwithpub(byte[] input) {
		byte[] n = {(byte)0x80,(byte)0x04,(byte)0x73,(byte)0xb0,(byte)0x01,(byte)0x7d,(byte)0x01,(byte)0x35,(byte)0xdb,(byte)0x6c,(byte)0x6e,(byte)0x80,(byte)0x55,(byte)0x4d,
				(byte)0x43,(byte)0x9c,(byte)0x11,(byte)0x30,(byte)0xe6,(byte)0xf6,(byte)0xf5,(byte)0xf9,(byte)0xa6,(byte)0x6f,(byte)0x9b,(byte)0xfa,(byte)0xac,(byte)0x7c,(byte)0x1d,
			    (byte)0x0d,(byte)0x1d,(byte)0xc9,(byte)0xca,(byte)0x48,(byte)0xce,(byte)0x0a,(byte)0x2e,(byte)0x53,(byte)0x13,(byte)0xf2,(byte)0x66,(byte)0xc3,(byte)0xc4,(byte)0xce,
			    (byte)0x73,(byte)0x59,(byte)0x71,(byte)0xdf,(byte)0x48,(byte)0xc9,(byte)0xc6,(byte)0x0a,(byte)0x59,(byte)0x97,(byte)0xb3,(byte)0xfa,(byte)0x5c,(byte)0xec,(byte)0x51,
			    (byte)0xb0,(byte)0x6d,(byte)0xb2,(byte)0x2c,(byte)0x4a,(byte)0x49,(byte)0x61,(byte)0x4d,(byte)0xcd,(byte)0xd4,(byte)0x3b,(byte)0xdc,(byte)0x61,(byte)0x12,(byte)0x9f,
			    (byte)0xb3,(byte)0xe8,(byte)0x46,(byte)0x02,(byte)0xbc,(byte)0xb4,(byte)0xc3,(byte)0xfd,(byte)0xac,(byte)0x17,(byte)0xac,(byte)0x8b,(byte)0x6b,(byte)0x99,(byte)0x85,
			    (byte)0xa5,(byte)0xd5,(byte)0x30,(byte)0xa6,(byte)0x6a,(byte)0x6f,(byte)0x9d,(byte)0x76,(byte)0x3c,(byte)0x54,(byte)0xc6,(byte)0x33,(byte)0x05,(byte)0x88,(byte)0xfc,
			    (byte)0x51,(byte)0x93,(byte)0x81,(byte)0xc7,(byte)0x09,(byte)0xc3,(byte)0xcb,(byte)0xca,(byte)0x75,(byte)0x16,(byte)0xb9,(byte)0x50,(byte)0x63,(byte)0x85,(byte)0xb5,
			    (byte)0xa3,(byte)0xeb,(byte)0x77,(byte)0x8d,(byte)0x23,(byte)0xab,(byte)0x67,(byte)0xf0,(byte)0xc3,(byte)0x1e,(byte)0x9a,(byte)0x0a,(byte)0x3f,(byte)0xd4,(byte)0x7c,
			    (byte)0xd3,(byte)0x7d,(byte)0xb9,(byte)0xed,(byte)0x66,(byte)0x4c,(byte)0x46,(byte)0x33,(byte)0x3f,(byte)0x5c,(byte)0x94,(byte)0xa2,(byte)0x17,(byte)0x1a,(byte)0xc3,
			    (byte)0x4f,(byte)0x30,(byte)0xca,(byte)0x8e,(byte)0x11,(byte)0x23,(byte)0x79,(byte)0x84,(byte)0x4d,(byte)0x33,(byte)0xbb,(byte)0x40,(byte)0xdd,(byte)0xb6,(byte)0xea,
			    (byte)0x08,(byte)0x3a,(byte)0xd9,(byte)0x5d,(byte)0x24,(byte)0x78,(byte)0x15,(byte)0xde,(byte)0x41,(byte)0x48,(byte)0x90,(byte)0xff,(byte)0x3a,(byte)0x8f,(byte)0x07,
			    (byte)0x6b,(byte)0x78,(byte)0x15,(byte)0xce,(byte)0xb1,(byte)0x56,(byte)0x06,(byte)0x35,(byte)0x70,(byte)0x7d,(byte)0x17,(byte)0xf0,(byte)0x98,(byte)0xfd,(byte)0x7e,
			    (byte)0x16,(byte)0x43,(byte)0x88,(byte)0xae,(byte)0x38,(byte)0xeb,(byte)0xa0,(byte)0x61,(byte)0x13,(byte)0x0e,(byte)0x73,(byte)0xcf,(byte)0x9c,(byte)0x44,(byte)0x0c,
			    (byte)0x1d,(byte)0x2a,(byte)0xc8,(byte)0x6a,(byte)0xca,(byte)0x1e,(byte)0xd4,(byte)0x0e,(byte)0xf3,(byte)0x36,(byte)0x4f,(byte)0x85,(byte)0x3b,(byte)0xda,(byte)0x35,
			    (byte)0xd4,(byte)0x51,(byte)0x1a,(byte)0xe6,(byte)0x28,(byte)0xe4,(byte)0xdc,(byte)0xea,(byte)0xc5,(byte)0x19,(byte)0x4c,(byte)0xce,(byte)0x01,(byte)0xf7,(byte)0xda,
			    (byte)0x38,(byte)0xe5,(byte)0x89,(byte)0x66,(byte)0x44,(byte)0x41,(byte)0x88,(byte)0x12,(byte)0x7e,(byte)0xbb,(byte)0x4b,(byte)0x98,(byte)0x7c,(byte)0xa6,(byte)0xda,
			    (byte)0x2e,(byte)0x03};
		
		byte[] e=toBytes(65537);
		DebugPrint.printBuffer(e);
		DebugPrint.printBuffer(n);
		DebugPrint.printInt(n.length);
		RsaAlg alg = RsaAlg.create();
		alg.generateKeys((short)256);
		alg.setHashAlg(RsaAlg.HASH_TYPE_SHA256);
		alg.setPaddingScheme(RsaAlg.PAD_TYPE_OAEP);
		byte[] output=new byte[256];
		alg.setKey(n, (short)0, (short)n.length, e,(short) 0,(short)e.length);
		byte[] lastbytes=new byte[16];
		int size=alg.encryptComplete(input, (short)0, (short)input.length, output, (short)0);
		ArrayUtils.copyByteArray(output, (short)size-16, lastbytes, (short)0, (short)16);
		DebugPrint.printInt(size);
		DebugPrint.printBuffer(output);
		DebugPrint.printString("-------------------------");
		DebugPrint.printBuffer(lastbytes);
		return output;
	}
	public static byte[] toBytes(int a) {
		byte[] ret = new byte[]{ (byte) (a), (byte) (a >> 8), (byte) (a >> 16), (byte) (a >> 24)};
		return ret;
		}

}