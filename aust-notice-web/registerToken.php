<?php
	if(isset($_POST["Token"])){
		$token = $_POST["Token"];
		echo $token;
		$connectionInfo = array("UID" => "igeniusarnob@arnobkarmokar", "pwd" => "@rnoB2016", "Database" => "ArnobDB", "LoginTimeout" => 30, "Encrypt" => 1, "TrustServerCertificate" => 0);
		$serverName = "tcp:arnobkarmokar.database.windows.net,1433";
		$conn = sqlsrv_connect($serverName, $connectionInfo);	
		$sql = "INSERT INTO austNoticeFirebaseUsers (token) VALUES ('$token')";
		$result = sqlsrv_query( $conn, $sql);
		sqlsrv_free_stmt( $result);  
		sqlsrv_close( $conn); 
	}
