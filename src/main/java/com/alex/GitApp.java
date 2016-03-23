package com.alex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.errors.UnmergedPathException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class GitApp {
	
	  @Value("${git.username}")
	  String username;

	  @Value("${git.password}")
	  String password;

	  @Value("${git.url}")
	  String url;
	  
	  private Git git;
	  private CredentialsProvider cp;

	@RequestMapping("/git/init") 
	public void init() throws Throwable {
	        // credentials
	        cp = new UsernamePasswordCredentialsProvider(username, password);
	        // clone
	        //TODO you might want to clone in a per web session folder or equivalent
	        File dir = new File("/tmp/cf-sharedfs");

	        deleteDir(dir);
	        dir.mkdir();
	        
	        Logger.getLogger(GitApp.class.getName()).log(Level.INFO, "Cloning "+url);
	        CloneCommand cc = new CloneCommand()
	                .setCredentialsProvider(cp)
	                .setDirectory(dir)
	                .setTimeout(15)
	                .setURI(url);
	        git = cc.call();
	        Logger.getLogger(GitApp.class.getName()).log(Level.INFO, "Repository is ready");
			
	        /*
	        Repository existingRepo = new FileRepositoryBuilder()
	        	    .setGitDir(new File("/tmp/cf-sharedfs/.git"))
	        	    .build();
	        Git git = new Git(existingRepo);
	       */
	    
	}
	
	@RequestMapping("/git/put")
	public String writeFile() throws Throwable {
		if (git == null) init();
	
		    // add (optional)
	        new File("/tmp/cf-sharedfs/jgit").mkdirs();
	        git.add().addFilepattern("jgit").call();
	        
	        // new file
	        File foo = File.createTempFile("pre", "post", new File("/tmp/cf-sharedfs/jgit"));
	        
	        Logger.getLogger(GitApp.class.getName()).log(Level.INFO, "Adding file "+foo.getName()); 
	        		
	        // add
	        AddCommand ac = git.add();
	        ac.addFilepattern("jgit/"+foo.getName());
	        ac.call();
	        
	        // commit
	        CommitCommand commit = git.commit();
	        commit.setCommitter("Program", "pg@email.com")
	                .setMessage("add file "+foo.getName());
	        commit.call();
	        
	        // push
	        PushCommand pc = git.push();
	        pc.setCredentialsProvider(cp)
	                //.setForce(true)//very dangerous if true - it would overwrite all the remote
	                .setPushAll();
	        try {
	        	Iterable<PushResult> pr = pc.call();
	            Iterator<PushResult> it = pr.iterator();
	            if(it.hasNext()){
	            	Logger.getLogger(GitApp.class.getName()).log(Level.INFO, "push " +it.next().toString());	                
	            }
	        } catch (InvalidRemoteException e) {
	            e.printStackTrace();
	        }

	        return foo.getName();
	    }

	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}	
	
	
}
