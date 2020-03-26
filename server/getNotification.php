<?php
include_once dirname(__FILE__)."/DbOps.php";
$response = array();

if($_SERVER['REQUEST_METHOD'] == 'GET') {
    if(isset($_GET['userId'])) {
      $db = new DbOps();
      $notification = $db->getAndClearNotification($_GET['userId']);
      if(strlen($notification) != 0){
            // the image is now on the server, need to notify people that follow the author
            // hence it's better in the database if the follows section contains the people that follow that person.
            $response['error'] = false;
            $response['message'] = $notification." uploaded new pictures!";
        }else{
            $response['error'] = false;
            $response['message'] = "";
        }

		} else {
        echo "Missing field(s)";
        echo "[userId status: ",isset($_GET['userId']), "]";
    }
}else{
    $response['error'] = true;
    $response['message'] = "invalid request";
}
echo json_encode($response);
?>
