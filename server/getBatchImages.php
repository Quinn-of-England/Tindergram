<?php
include_once dirname(__FILE__)."/DbOps.php";
$response = array();
$images = array();
// takes id of the user
  if($_SERVER['REQUEST_METHOD'] == 'GET') {
                if(isset($_GET['id'])) {
                    $db = new DbOps();
                    $loggedId = $_GET['id'];
                    //get image rows of all pictures that were posted by users that the logged user follow
                    $regex = "'%".$loggedId.",%'";
                    $preparedStatement = "SELECT * FROM pictures WHERE authorId IN (SELECT id FROM users WHERE isFollowedBy LIKE $regex)";
                    //echo $preparedStatement;
                    $stmt = $db->conn->prepare($preparedStatement);
                    if($stmt->execute()){
			                  // $stmt->store_result();
			                  // $stmt->bind_result($imageRowsFromFollowed);
			                  // $stmt->fetch();

                        //will send a maximum of 5 pictures at once
                        $imagesLeft = 5;
                        $imageIndex = -1;
                        $imageRowsFromFollowed = $stmt->get_result();
                        while ($row = $imageRowsFromFollowed->fetch_assoc() and $imagesLeft >= 1) {
                          //echo 'ID: '.$row['id'].'<br>';
                          //array_push($images, $row['image']);
                          $imageIndex = 5 - $imagesLeft;
                          $response[$imageIndex] = base64_encode($row['image']);
                          $imagesLeft--;
                        }

                        $response['error'] = 0;
                        $response['size'] = $imageIndex + 1;
                        $response['images'] = $images;

                        //for now, just select the first five images of the mysqli_more_results
                        //TODO ckeck the image ids to know whether they were viewed or not

                        //free results
                        $stmt->free_result();
                        //close statement
                        $stmt->close();
                        //header("Content-Type: image/jpeg");

			              } else {
                      $response['error'] = 1;
                      $response['message'] = "Could not fetch the array of followed users OR fetch pictures posted by those users";
                    }

                }else{
                    echo "Missing field";
                    echo "[id status: ",isset($_GET['id']), "]";
                }
	}else{
    $response['error'] = 1;
    $response['message'] = "invalid request";
  }
echo json_encode($response);
?>
