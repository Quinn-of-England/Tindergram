<?php
include_once dirname(__FILE__)."/DbOps.php";
$response = array();

if($_SERVER['REQUEST_METHOD'] == 'POST') {
    if(isset($_POST['id']) and isset($_POST['comment']) and isset($_POST['username'])) {
          $db = new DbOps();
          $imageId = $_POST['id'];

          if($db->isUsernameExist($_POST['username'])){
            if($db->addComment($imageId, $_POST['username'], $_POST['comment'])){
                $response["error"] = 0;
                $response["message"] = "success";
            }else{
                echo $db->conn->error;
                return false;
            }
          } else {
            $response['error'] = 1;
            $response['message'] = "username does not exist!";
          }
		} else {
        echo "Missing field(s)";
        echo "[id status: ",isset($_POST['id']), "]";
        echo "[comments status: ",isset($_POST['comments']), "]";

    }
}else{
    $response['error'] = 1;
    $response['message'] = "invalid request";
}
echo json_encode($response);
?>
