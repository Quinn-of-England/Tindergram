<?php
include_once dirname(__FILE__)."/DbOps.php";
$response = array();

if($_SERVER['REQUEST_METHOD'] == 'POST') {
    if(isset($_POST['followerUser']) and isset($_POST['followedUser'])) {
      $db = new DbOps();

      if($db->isUsernameExist($_POST['followerUser']) and $db->isUsernameExist($_POST['followedUser'])){
          $followerId = $db->getIdByUsername($_POST['followerUser'])['id'];
          $followedId = $db->getIdByUsername($_POST['followedUser'])['id'];

          if($db->canFollow($followerId,$followedId)){
            if($db->follows($followerId, $followedId)){
                  // the image is now on the server, need to notify people that follow the author
                  // hence it's better in the database if the follows section contains the people that follow that person.
                  $response['error'] = false;
                  $response['message'] = "success";
              }else{
                  $response['error'] = true;
                  $response['message'] = "Error following user";
              }
          } else {
            $response['error'] = true;
            $response['message'] = "You're already following ".$_POST['followedUser'];
          }
      } else {
        $response['error'] = true;
        $response['message'] = "user(s) don't exist";
      }

		} else {
        echo "Missing field(s)";
        echo "[followerUser status: ",isset($_POST['followerUser']), "]";
        echo "[followedUser status: ",isset($_POST['followedUser']), "]";
    }
}else{
    $response['error'] = true;
    $response['message'] = "invalid request";
}
echo json_encode($response);
?>
