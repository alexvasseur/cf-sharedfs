# cf-sharedfs

## Overview

A simple Spring Boot project that shows:
- Git as a simple shared content folder between apps / instances of apps
- SSHFS as a simple shared content folder between apps / instances of apps

The use case targets application that do need some basic shared storage and have to be deployed into container environment such as Cloud Foundry.

Your best choice would be to use a Cloud Native storage API such as S3 using Amazon on public cloud, EMC ECS and the Pivotal Cloud Foundry service broker to have S3 as a service on premise, etc.

## How to build & run
### Quickstart
```
review src/main/resources/application.yml
you can leave it as-is and override and command line or with an external yml file (handled by Spring Boot)
mvn clean package
java -jar target/...jar [ --git.password=... ]
cf push sshfs -p target/...jar
```
### Setup of backends
The default git repo for content is [https://github.com/avasseur-pivotal/cf-sharedfs-content]. You need to use yours with your credentials for the app to write in it.
The default sshfs endpoint is a private (LAN) linux in which there is a public RSA key setup in .ssh/authorized_keys . You need to use yours for the app to mount it.

### Application endpoint
The app provides basic HTTP endpoint to interact with setting up the shared filesystem and testing access
```
curl -X GET cfapp.domain/git/init
curl -X GET cfapp.domain/git/put

curl -X GET cfapp.domain/sshfs/init
curl -X GET cfapp.domain/sshfs/ls
```

## Pros / Cons of Git

Git is used by Netflix oss / Spring cloud for storing application config.
Thoughtworks wrote about using Git as a content management system.
This said Git read/write semantics are limited:
- read is full repo checkout unless you use many branches (e.g. one branch per business context)
- accessing just one file is possible with git archive but that adds complexity (not implemented in this demo app)

## Pros / Cons of SSHFS

Cloud Foundry container does have sshfs and FUSE in it.
Pivotal has a SSHFS server side implementation for you to try that is multitenant and with a Cloud Foundry service broker but you can run this app with any VM exposing files over ssh (as any Linux with ssh would do)
Cloud Foundry teams are working on a disk-as-a-service for container with pluggable block storage backend as well.
(contact me for more details)

Once all this is ready, FUSE & sshfs in container might become irrelevant as it is a less scalable approach to the same problem space.
This said, sshfs works in minutes. This apps shows a Java init, but another project shows also a shell based init using Cloud Foundry .profile.d/ stager hook

 

