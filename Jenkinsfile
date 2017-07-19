node {
	stage('AntLib') {
		echo 'I will build Ant Lib'
	}

	stage('HeadlessPlugin') {
		echo 'Now I will build plugin'
	}

	stage('Results') {
		archive '**/*.xml'
	}
}
