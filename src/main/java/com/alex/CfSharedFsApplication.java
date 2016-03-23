package com.alex;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
public class CfSharedFsApplication {
	
	public GitApp mainApp;
	
	@Autowired
	public void setMainApp(GitApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void run() {
		System.err.println("RUN");
			try {
				mainApp.init();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	public static void main(String[] args) {
		
		//SshFsInit.sshfs();
//		try {
//			mainApp.main2(null);
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
		
			SpringApplication.run(CfSharedFsApplication.class, args);//.getBean(CfSharedFsApplication.class).run();
		
	}
}
