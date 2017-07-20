properties([pipelineTriggers([githubPush()])])

node {
	stage('AntLib') {
		echo 'I will build Ant Lib'
		withEnv( ["ANT_HOME=${tool DefaultCam}"] ) {		
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
