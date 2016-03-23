package com.alex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
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

	private String shell(String cmd) throws Throwable {
		String s = null;
		Process p = null;
		BufferedReader br = null;
		p = Runtime.getRuntime().exec(cmd);
		br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringBuffer out = new StringBuffer();
		out.append("shell> " + cmd + "\n");
		while ((s = br.readLine()) != null)
			out.append(s).append('\n');
		p.waitFor();
		System.out.println("#### Executing command ls with exit: " + p.exitValue());
		p.destroy();
		br.close();
		return out.toString();
	}

	@RequestMapping("/sshfs/info")
	public void info() {
		System.out.println(privateKey);
	}

	@RequestMapping("/sshfs/init")
	public void sshfs() throws Throwable {
		shell("mkdir -p /home/vcap/sshfs");
		shell("mkdir -p /home/vcap/.ssh");

		// "cat <<EOF > /home/vcap/.ssh/sshfs_id_rsa\n"+privateKey+"\nEOF",
		File f = new File("/home/vcap/.ssh/sshfs_id_rsa");
		FileWriter fw = new FileWriter(f);
		fw.write(privateKey);
		fw.close();
		
		
		shell("chmod 600 /home/vcap/.ssh/sshfs_id_rsa");
		shell("touch /home/vcap/app/known_hosts");
		shell("chmod 600 /home/vcap/app/known_hosts");

		shell("sshfs " + userHostPath
				+ " /home/vcap/sshfs -o IdentityFile=/home/vcap/.ssh/sshfs_id_rsa -o StrictHostKeyChecking=no -o UserKnownHostsFile=/home/vcap/app/known_hosts -o idmap=user -o compression=no -o sshfs_debug");
	}

	@RequestMapping("/sshfs/ls")
	public String ls() throws Throwable {
		return shell("ls -l /home/vcap/sshfs");
	}

}
