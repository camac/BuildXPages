BuildXPages
===========

This project is a collection of tools which are useful in the case of building XPages applications using a Continous Integration server like Jenkins.

* Augmented 'Headless' designer plugin to allow better feedback 
* Ant library of common tasks that are useful when building XPages apps
* Jenkins plugin to use build Stpes

Using the Ant Library
---------------------

Intro to Ant

If you don't know about apache ant, it is basically a tool to define steps in a process and execute them! This is very handy for deployment steps etc.

You have a whole bunch of standard tasks which can be anything from copying a file, to compiling some code, jarring it up, running a process whatever.
You can also make your own tasks and supply them as an ant library which is what I have done here.

The basics of ant, is that you create an xml file which is your build script. It is usually called 'build.xml' but it can be another name.
Within your build script, you define sub-routines which are called targets. Targets can depend on other targets, so if 'last' depends on 'middle', and 'middle' depends on 'first', when you tell ant to execute the 'last' target, it will actually run 'first' then 'middle' then 'last'.

Your build script can define a default target to run in the case that you have not specified a target to run.


### Tasks 

- buildnsf
  builds an NSF by connecting to Domino Design with Headless Plugin    
- copynsf
  copies an NSF from the local filesystem to a server
- deletensf
  deletes an NSF from a server
- settemplatenames
  sets or clears the inheritfrom and ismaster template names of an NSF
- scxd
  sets or clears the single copy xpages design settings of an NSF
- restarthttp **NOT FINISHED**
  sends a 'restart task http' command to a domino server console
- refreshdbdesign
  refreshs the design of a database
- generatesitexml
  generates a site.xml based on the supplied directory

### Using the Library in an Ant Build Script

The BuildXPages library depends on the Java Native Access Library
To use the BuildXPages Library in an Ant Build script, you need to:

1. Make the BuildXPagesAntLib.jar available to ant
2. Make the Java Native Access jar available to ant
3. Define in your build script that you want to use this library

You have a couple of options here, you can install the jars 'Once and for all', or configure on a project by project basis.
You probably want to do it project by project, this way you can include these libraries in your Source Code Management repository (Git, Mercurial etc.)

##### Per Project Basis

Easiest way is to create a 'lib' sub folder in your project, and put both BuildXPagesAntLib.jar and jna-4.1.0.jar in this folder

Then your Build script needs to declare that it wants to use the libary, see the file buildTemplate.xml for the example.

###### Running

When you run your build script, you will need to include a -lib argument which defines the lib folder as a place to look for libraries.

e.g. if we 
```
ant -buildfile <yourBuildFile.xml> -lib lib <yourtarget>
```

##### Once and for all

Install BuildXPagesAntLib.jar and jna-4.1.0.jar into either
* ANT_HOME/lib
* ${user.home}/.ant/lib
