pipeline{

  agent any  

  stages {
    stage('Checkout') {
      checkout scm
    }

  	stage('AntLib') {
	  	echo "I will build Ant Lib on ${env.BRANCH_NAME}"
		  def antVersion = 'DefaultCam'
  		withEnv( ["ANT_HOME=${tool antVersion}"] ) {		
	  		bat '%ANT_HOME%/bin/ant.bat -buildfile com.gregorbyte.buildxpages.ant/build.xml -DnotesProgDir=${env.NOTES_PROGDIR} compilejar'
		  }
  	}

	  stage('HeadlessPlugin') {
		  echo 'Now I will build plugin'
  	}

	  stage('Results') {
		  archive '**/BuildXPagesAntLib.jar'

  	}
  }

  post {
    changed {
   		emailext body: "BuildXPages ${currentBuild.result}", subject: "BuildXPages ${currentBuild.result}", to: 'cgregor@jord.com.au'      
    }
    failure {
  		emailext body: 'BuildXPages Failed', subject: 'BuildXPages Failed', to: 'cgregor@jord.com.au'      
    }
  }

}
