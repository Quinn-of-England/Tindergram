<?php
include_once dirname(__FILE__)."/DbOps.php";
$response = array();

if($_SERVER['REQUEST_METHOD'] == 'POST') {
    if(isset($_FILES['imageFile']) and isset($_POST['authorId']) and isset($_POST['likes']) and isset($_POST['comments'])) {
      $db = new DbOps();
 	    $imageData = file_get_contents($_FILES['imageFile']["tmp_name"]);
      $name = $_FILES['imageFile']["name"];
			$type = $_FILES['imageFile']["type"];
			$null = NULL;

      $stmt = $db->conn->prepare("INSERT INTO `pictures` (`image`,`name`,`type`, `authorId`, `likes`, `comments`) VALUES (?,?,?,?,?,?);");
			$stmt->bind_param("bsssss",$null,$name,$type,$_POST['authorId'],$_POST['likes'],$_POST['comments']);
			$stmt->send_long_data(0, $imageData);
      if($stmt->execute()){
          // the image is now on the server, need to notify people that follow the author
          // hence it's better in the database if the follows section contains the people that follow that person.
            $db->notifyAllThatFollowId($_POST['authorId']);
            $response["error"] = 0;
            $response["message"] = "success";
      }else{
	         echo $db->conn->error;
           }

		} else {
        echo "Missing field(s)";
        echo "[image status: ",isset($_POST['image']), "]";
        echo "[id status: ",isset($_POST['authorId']), "]";
        echo "[likes status: ",isset($_POST['likes']), "]";
        echo "[comments status: ",isset($_POST['comments']), "]";

    }
}else{
    $response['error'] = 1;
    $response['message'] = "invalid request";
}
echo json_encode($response);
?>
