<?php
	require_once('simple_html_dom.php');

	function push_notification($tokens,$message){
		$url = "https://fcm.googleapis.com/fcm/send";
		$fields = array(
			'registration_ids' => $tokens,
			'data' => $message
		);
		$headers = array(
			'Authorization:key = AAAAxEkrsXw:APA91bEo7LPQ72fKNFQa7YmCKMXGK5jgCrCat3RhdNtDQzCRrJV1YK_oqX0ZVnfHoSzLc_3xDDda3IK5yamSRbRcXKMgSfM_kTC77ksVHshK7b0DmYGKIxX77nfN2BwpsWvDaVh8DRqB',
			'Content-Type: application/json'
			);
	   $ch = curl_init();
       curl_setopt($ch, CURLOPT_URL, $url);
       curl_setopt($ch, CURLOPT_POST, true);
       curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
       curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
       curl_setopt ($ch, CURLOPT_SSL_VERIFYHOST, 0);
       curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
       curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
       $result = curl_exec($ch);
	   if ($result === FALSE) {
           die('Curl failed: ' . curl_error($ch));
       }
       curl_close($ch);
       return $result;
	}

	$connectionInfo = array("UID" => "igeniusarnob@arnobkarmokar", "pwd" => "@rnoB2016", "Database" => "ArnobDB", "LoginTimeout" => 30, "Encrypt" => 1, "TrustServerCertificate" => 0);
	$serverName = "tcp:arnobkarmokar.database.windows.net,1433";
	$conn = sqlsrv_connect($serverName, $connectionInfo);
	$sql = "SELECT token FROM austNoticeFirebaseUsers";
	$stmt = sqlsrv_query( $conn, $sql);
	$tokens = array();
	while( $row = sqlsrv_fetch_array( $stmt, SQLSRV_FETCH_ASSOC) ) {
      $tokens[] = $row["token"];
	}
	sqlsrv_free_stmt( $stmt);
	sqlsrv_close( $conn);

	$html = file_get_html('http://www.aust.edu/news_events.htm');
	$current_notice_title = $html->find('#AutoNumber6',0)->children(2)->children(0)->children(0)->children(0)->children(1)->children(0)->children(0)->innertext;
	$current_notice_url = $html->find('#AutoNumber6',0)->children(2)->children(0)->children(0)->children(0)->children(1)->children(0)->children(0)->getAttribute('href');
	if($current_notice_url == "javascript:void(0)"){
	    $current_notice_url = $html->find('#AutoNumber6',0)->children(2)->children(0)->children(0)->children(0)->children(1)->children(0)->children(0)->getAttribute('onclick');
	    $current_notice_url = str_replace("window.open('", "", $notice_url);
	    $current_notice_url = substr($notice_url, 0,strpos($notice_url, "htm")+3);
	}
	$current_notice = array(
	      'title' => $current_notice_title,
	      'url' => $current_notice_url
	);
	$previous_notice = json_decode(file_get_contents("previous.json"));
	$previous_notice_title = $previous_notice->title;
	$previous_notice_url = $previous_notice->url;
	echo "Last Notice: " . $previous_notice_title . ' URL: ' . $previous_notice_url;
	$message = array(
		"message" => $current_notice_title,
		"notice_url" => $current_notice_url
		);
	if($current_notice_title!=$previous_notice_title){
	  	echo '</br>' . " New Notice! ";
		$message_status = push_notification($tokens, $message);
		echo $message_status;
	  	file_put_contents("previous.json",json_encode($current_notice));
		copy("previous.json","../../../../../../home/site/wwwroot/App_Data/jobs/triggered/aust-notice-push/previous.json");
	} else {
	  	echo '</br>' . " No New Notice! :( ";
	}
?>
