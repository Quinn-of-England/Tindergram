<?php
include_once dirname(__FILE__)."/DbOps.php";
$response = array();
$images = array();
// takes id of the user
  if($_SERVER['REQUEST_METHOD'] == 'GET')
  {
      if(isset($_GET['id']))
      {
          //isset($_GET['imageIdsAlreadySeen']) not mandatory, will simply get same pictures if you don't include it
          $db = new DbOps();
          $loggedId = $_GET['id'];
          //get image rows of all pictures that were posted by users that the logged user follow
          $regex1 = "'%,".$loggedId.",%'";
          $regex2 = "'".$loggedId.",%'";
          $preparedStatement = "SELECT * FROM pictures WHERE authorId IN (SELECT id FROM users WHERE isFollowedBy LIKE $regex1 OR isFollowedBy LIKE $regex2)";
          //echo $preparedStatement;
          $stmt = $db->conn->prepare($preparedStatement);
          if($stmt->execute())
          {
              // $stmt->store_result();
              // $stmt->bind_result($imageRowsFromFollowed);
              // $stmt->fetch();

              //will send a maximum of 5 pictures at once
              $imagesLeft = 5;
              $imageIndex = -1;
              $imageRowsFromFollowed = $stmt->get_result();
              while ($row = $imageRowsFromFollowed->fetch_assoc() and $imagesLeft >= 1)
              {
                $pattern1 = "/^".$row['id'].",/";
                $pattern2 = "/,".$row['id'].",/";
                //echo 'ID: '.$row['id'].'<br>';
                //array_push($images, $row['image']);
                if(preg_match($pattern1, $_GET['imageIdsAlreadySeen']) == 0 and preg_match($pattern2, $_GET['imageIdsAlreadySeen']) == 0)
                {
                  $imageIndex = 5 - $imagesLeft;
                  $response[$imageIndex]['image'] = base64_encode($row['image']);
                  $response[$imageIndex]['id'] = $row['id'];
                  $response[$imageIndex]['authorId'] = $row['authorId'];
                  $response[$imageIndex]['likes'] = $row['likes'];
                  $response[$imageIndex]['comments'] = $db->parseComments($row['comments']);
                  $imagesLeft--;
                }

              }

              $response['error'] = 0;
              $response['size'] = $imageIndex + 1;
              $response['message'] = "Success";

              $stmt->free_result();
              //close statement
              $stmt->close();

          } else
          {
            $response['error'] = 1;
            $response['message'] = "Could not fetch the array of followed users OR fetch pictures posted by those users";
          }

      }else
      {
          echo "Missing field";
          echo "[id status: ",isset($_GET['id']), "]";
      }
	}else
  {
    $response['error'] = 1;
    $response['message'] = "invalid request";
  }
echo json_encode($response);
?>
