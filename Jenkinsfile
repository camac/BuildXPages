properties([pipelineTriggers([githubPush()])])

node {

	checkout scm

	stage('AntLib') {
		echo 'I will build Ant Lib'
		def antVersion = 'DefaultCam'
		withEnv( ["ANT_HOME=${tool antVersion}"] ) {		
			bat '%ANT_HOME%/bin/ant.bat -buildfile com.gregorbyte.buildxpages.ant/build.xml'
		}
	}

	stage('HeadlessPlugin') {
		echo 'Now I will build plugin'
	}

	stage('Results') {
		archive '*.md'
	}
}
