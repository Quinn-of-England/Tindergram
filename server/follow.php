<?php
include_once dirname(__FILE__)."/DbOps.php";
$response = array();

if($_SERVER['REQUEST_METHOD'] == 'POST') {
    if(isset($_POST['followerId']) and isset($_POST['followedId'])) {
      $db = new DbOps();

      if($db->follows($_POST['followerId'], $_POST['followedId'])){
            // the image is now on the server, need to notify people that follow the author
            // hence it's better in the database if the follows section contains the people that follow that person.
            $response["error"] = 0;
            $response["message"] = "success";
        }else{
            echo $db->conn->error;
            return false;
        }

		} else {
        echo "Missing field(s)";
        echo "[FollowerId status: ",isset($_POST['followerId']), "]";
        echo "[FollowedId status: ",isset($_POST['followedId']), "]";
    }
}else{
    $response['error'] = 1;
    $response['message'] = "invalid request";
}
echo json_encode($response);
?>
