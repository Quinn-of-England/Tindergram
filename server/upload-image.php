<?php
include_once dirname(__FILE__)."/DbOps.php";
$response = array();

if($_SERVER['REQUEST_METHOD'] == 'POST') {
    if(isset($_FILES['image']) and isset($_POST['id']) and isset($_POST['likes']) and isset($_POST['comments'])) {
      $db = new DbOps();
			$imageData = file_get_contents($_FILES['image']["tmp_name"]);
      $name = $_FILES['image']["name"];
			$type = $_FILES['image']["type"];
			$null = NULL;

      $stmt = $db->conn->prepare("INSERT INTO `pictures` (`image`,`name`,`type`, `id`, `likes`, `comments`) VALUES (?,?,?,?,?,?);");
			$stmt->bind_param("bsssss",$null,$name,$type,$_POST['id'],$_POST['likes'],$_POST['comments']);
			$stmt->send_long_data(0, $imageData);
          if($stmt->execute()){
              $response["error"] = 0;
              $response["message"] = "success";
          }else{
              echo $db->conn->error;
              return false;
          }

		} else {
        echo "Missing field(s)";
        echo "[image status: ",isset($_POST['image']), "]";
        echo "[id status: ",isset($_POST['id']), "]";
        echo "[likes status: ",isset($_POST['likes']), "]";
        echo "[comments status: ",isset($_POST['comments']), "]";

    }
}else{
    $response['error'] = 1;
    $response['message'] = "invalid request";
}
echo json_encode($response);
?>
