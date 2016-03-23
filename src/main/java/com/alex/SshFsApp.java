package com.alex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class SshFsApp {
	
	@Value("${sshfs.userHostPath}")
	public String userHostPath;

	@Value("${sshfs.privateKey}")
	public String privateKey;
	
	@RequestMapping("/sshfs/info")
	public void info() {
		System.out.println(privateKey);
	}

	@RequestMapping("/sshfs/init")
	public void sshfs() throws Throwable {
		String s = null;
		Process p = null;
		BufferedReader br = null;
			String[] cmds = new String[]{
				"mkdir -p /home/vcap/sshfs",
				"mkdir -p /home/vcap/.ssh",
				"cat '"+privateKey+"' > /home/vcap/.ssh/sshfs_id_rsa",
				"chmod 600 /home/vcap/.ssh/sshfs_id_rsa",
				"touch /home/vcap/app/known_hosts",
				"chmod 600 /home/vcap/app/known_hosts"
			};
			
			for (String cmd : cmds) {
		    p = Runtime.getRuntime().exec(cmd);
		    br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    while((s = br.readLine()) != null)
		        System.out.println("line: " + s);
		    p.waitFor();
		    System.out.println ("#### Executing ["+cmd+"] with exit: " + p.exitValue());
		    p.destroy();
		    br.close();
			}

		    p = Runtime.getRuntime().exec("sshfs "+userHostPath+" /home/vcap/sshfs -o IdentityFile=/home/vcap/.ssh/sshfs_id_rsa -o StrictHostKeyChecking=no -o UserKnownHostsFile=/home/vcap/app/known_hosts -o idmap=user -o compression=no -o sshfs_debug");
		    br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    while((s = br.readLine()) != null)
		        System.out.println("line: " + s);
		    p.waitFor();
		    System.out.println ("#### Executing command sshfs with exit: " + p.exitValue());
		    p.destroy();
		    br.close();
		}
		
		@RequestMapping("/sshfs/ls")
		public String ls() {
			String s = null;
			Process p = null;
			BufferedReader br = null;
			try {
		    p = Runtime.getRuntime().exec("ls -l /home/vcap/sshfs");
		    br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    StringBuffer out = new StringBuffer();
		    while((s = br.readLine()) != null)
		        out.append(s).append('\n');
		    p.waitFor();
		    System.out.println ("#### Executing command ls with exit: " + p.exitValue());
		    p.destroy();
		    br.close();
		    return out.toString();
		}
		catch(IOException ex)
		{
		    ex.printStackTrace();
		}
		catch(InterruptedException ex)
		{
		    ex.printStackTrace();
		}
		finally
		{
		    try 
		    {
		        if(br != null)
		            br.close();
		    }
		    catch(IOException ex) 
		    {
		        ex.printStackTrace();
		    }
		}
			return "error";
	}
	
}
