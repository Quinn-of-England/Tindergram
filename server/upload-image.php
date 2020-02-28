<?php
include_once dirname(__FILE__)."/DbOps.php";
    	$response = array();	

	if($_SERVER['REQUEST_METHOD'] == 'POST') {
		if(isset($_POST['image']) and isset($_POST['id'])
		       	and isset($_POST['likes']) and isset($_POST['comments'])) {
					
			$db = new DbOps();
		
			if($db->saveImage($_POST['image'],$_POST['id'],$_POST['likes'],$_POST['comments'])) {
				$response["error"] = 0;
				$response["message"] = "successfuly filled table";
			}
			else{	
				$response["error"] = 1;
				$response["message"] = "error in database insertion";
			}

		}
		else {
			echo "Missing field";
		}
	}
	else 	{
		$response['error'] = 1;
		$response['message'] = "invalid request";
	}	
	echo json_encode($response);
?>
