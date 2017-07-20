pipeline{

  agent any  

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

  	stage('AntLib') {
      steps {
  	  	echo "I will build Ant Lib on ${env.BRANCH_NAME}"
  		  def antVersion = 'DefaultCam'
    		withEnv( ["ANT_HOME=${tool antVersion}"] ) {		
  	  		bat '%ANT_HOME%/bin/ant.bat -buildfile com.gregorbyte.buildxpages.ant/build.xml -DnotesProgDir=${env.NOTES_PROGDIR} compilejar'
  		  }
      }
  	}

	  stage('HeadlessPlugin') {
      steps {
  		  echo 'Now I will build plugin'
      }
  	}

	  stage('Results') {
      steps {
  		  archive '**/BuildXPagesAntLib.jar'
      }
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
