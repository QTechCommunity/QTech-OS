# Welcome to Q-OS!

<!--[![Build Status](https://travis-ci.org/jnode/jnode.svg?branch=master)](https://travis-ci.org/jnode/jnode)-->

In this file, you find the instructions needed to setup a Q-OS development environment.

## Sub-Projects

Q-OS has been divided into several sub-projects in order to keep it "accessible". These sub-projects are:

*Q-OS-All*    The root project where everything comes together  
*Q-OS-Cli*    Various command line commands  
*Q-OS-Core*   The core java classes, the Virtual Machine, the OS kernel and the Driver framework  
*Q-OS-Distr*  Tools and apps needed for a Q-OS "distribution"  
*Q-OS-FS*     The Filesystems and the various block device drivers  
*Q-OS-GUI*    The AWT implementation and the various video & input device drivers  
*Q-OS-Net*    The Network implementation and the various network device drivers  
*Q-OS-Shell*  The Command line shell and several system commands  
*Q-OS-Sound*  Future work  
*Q-OS-Textui* Text based AWT interface  

Each sub-project has the same directory structure:

`<subprj>/build`       All build results   
`<subprj>/descriptors` All plugin descriptors  
`<subprj>/lib`         All sub-project specific libraries  
`<subprj>/src`         All sources   
`<subprj>/.classpath`  The eclipse classpath file  
`<subprj>/.project`    The eclipse project file  
`<subprj>/build.xml`   The Ant buildfile  
`<subprj>/build-tests.xml` The Ant buildfile for running all tests of this subproject  

## Eclipse

Q-OS is usually developed in Eclipse. (It can be done without)

The various sub-projects must be imported into eclipse. Since they reference each other, it is advisably to import them all at the same time
using `Import -> Existing projects into workspace`.

## Building

Execute:

*On Windows*:  build.bat cd-x86-lite  
*On Linux*:    build.sh cd-x86-lite  

Or in Eclipse, execute the "cd-x86-lite" target of all/build.xml.

The build will result in the following files:

`all/build/cdroms/jnode-x86-lite.iso`         bootable CD image  
`all/build/cdroms/jnode-x86-lite.iso.vmx`     VMWare configuration file  

## Running

*On VMWare*: Open `all/build/cdroms/jnode-x86-lite.iso.vmx` and click Start.  
*On Linux*:  `qemu.sh`  

## Questions

If you have any questions, please post them to the forums at www.jnode.org
or to the IRC channel #Q-OS.org@irc.oftc.net
 
 -- The Q-OS Team --
 
