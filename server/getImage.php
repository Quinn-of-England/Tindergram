<?php
include_once dirname(__FILE__)."/DbOps.php";
$response = array();
$db = new DbOps();

  if($_SERVER['REQUEST_METHOD'] == 'GET')
  {
      if(isset($_GET['id']))
      {
          //$db = new DbOps();
          $id = $_GET['id'];

          $stmt = $db->conn->prepare("SELECT image FROM `pictures` WHERE id=?");
          $stmt->bind_param("s", $id);
          if($stmt->execute())
          {;
                $stmt->store_result();
                $stmt->bind_result($image);
                $stmt->fetch();

                header("Content-Type: image/jpeg");
                echo $image;
          }
      }else
      {
          echo "Missing field";
          echo "[id status: ",isset($_GET['id']), "]";
      }
	}else
  {
		echo "invalid request";
  }
?>
