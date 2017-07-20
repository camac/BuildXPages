pipeline{

  agent any  

  options {
    skipDefaultCheckout()
  }

  stages {

    stage('Checkout') {
      steps {
        checkout([
          $class: 'GitSCM',
          branches: [[name: 'develop']],
          extensions: scm.extensions,
          userRemoteConfigs: scm.userRemoteConfigs
        ])

      }

    }

  	stage('AntLib') {
      steps {
  	  	echo "I will build Ant Lib on ${env.BRANCH_NAME}"

    		withEnv( ["ANT_HOME=${tool 'DefaultCam'}"] ) {		
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
    success {
      emailext body: "BuildXPages Success ${currentBuild.currentResult}", subject: "BuildXPages ${currentBuild.currentResult}", to: 'cgregor@jord.com.au'      
    }
    changed {
   		emailext body: "BuildXPages ${currentBuild.currentResult}", subject: "BuildXPages ${currentBuild.currentResult}", to: 'cgregor@jord.com.au'      
    }
    failure {
  		emailext body: 'BuildXPages Failed', subject: "BuildXPages Failed", to: 'cgregor@jord.com.au'      
    }
  }

}
