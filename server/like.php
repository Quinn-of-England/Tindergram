<?php
include_once dirname(__FILE__)."/DbOps.php";
$response = array();

if($_SERVER['REQUEST_METHOD'] == 'POST') {
    if(isset($_POST['userId']) and isset($_POST['imageId'])) {
      $db = new DbOps();
      $userId = $_POST['userId'];
      $imageId= $_POST['imageId'];

      if($db->isUsernameExist($userId)){
          if($db->canLike($userId,$imageId)){
              if($db->like($imageId)){
                $response['error'] = false;
                $response['likeCount'] = $db->getLikes($imageId);
                $response['message'] = "success";
              } else {
                $response['error'] = true;
                $response['message'] = "Error while liking that picture";
              }
          } else {
            $response['error'] = true;
            $response['message'] = "You've already liked this image!";
          }
      } else {
        $response['error'] = true;
        $response['message'] = "user doesn't exist";
      }

		} else {
        echo "Missing field(s)";
        echo "[userId status: ",isset($_POST['userId']), "]";
        echo "[imageId status: ",isset($_POST['imageId']), "]";
    }
}else{
    $response['error'] = true;
    $response['message'] = "invalid request";
}
echo json_encode($response);
?>
