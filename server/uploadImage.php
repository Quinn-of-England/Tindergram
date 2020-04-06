<?php
include_once dirname(__FILE__)."/DbOps.php";
$response = array();

if($_SERVER['REQUEST_METHOD'] == 'POST')
{
    //$_POST['likes'] not used after all, since it is set to 0
    if(isset($_FILES['imageFile']) and isset($_POST['authorId']) and isset($_POST['likes']) and isset($_POST['comments']))
    {
        $db = new DbOps();
   	    $imageData = file_get_contents($_FILES['imageFile']["tmp_name"]);
        //not using name for now, it serves no purpose
        $name = $_FILES['imageFile']["name"];
  			$type = $_FILES['imageFile']["type"];
        $likes = 0;
  			$null = NULL;
        $username = $db->getUsernameById($_POST['authorId']);
        $comments = $db->formatComment($username, $_POST['comments']);

        $stmt = $db->conn->prepare("INSERT INTO `pictures` (`image`,`type`, `authorId`, `likes`, `comments`) VALUES (?,?,?,?,?);");
  			$stmt->bind_param("bssis",$null,$type,$_POST['authorId'],$likes,$comments);
  			$stmt->send_long_data(0, $imageData);
        if($stmt->execute())
        {
            // the image is now on the server, need to notify people that follow the author
            // hence it's better in the database if the follows section contains the people that follow that person.
              $db->notifyAllThatFollowId($_POST['authorId']);
              $response["error"] = 0;
              $response["message"] = "success";
        }else
        {
  	         echo $db->conn->error;
        }

		} else
    {
        echo "Missing field(s)";
        echo "[imageFile status: ",isset($_POST['imageFile']), "]";
        echo "[id status: ",isset($_POST['authorId']), "]";
        echo "[likes status: ",isset($_POST['likes']), "]";
        echo "[comments status: ",isset($_POST['comments']), "]";

    }
}else
{
    $response['error'] = 1;
    $response['message'] = "invalid request";
}
echo json_encode($response);
?>
