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
                    $stmt = $db->conn->prepare("SELECT * FROM pictures WHERE authorId IN (SELECT id FROM users WHERE isFollowedBy LIKE '%?,%')");
                    $stmt->bind_param("s", $loggedId);
                    if($stmt->execute()){
			                  $stmt->store_result();
			                  $stmt->bind_result($imageRowsFromFollowed);
			                  $stmt->fetch();

                        //for now, just select the first five images of the mysqli_more_results
                        //TODO ckeck the image ids to know whether they were viewed or not


                        header("Content-Type: image/jpeg");

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
