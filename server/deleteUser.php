<?php

include_once "DbOps.php";

$response = array();

if($_SERVER['REQUEST_METHOD']=='POST')
{
    if(isset($_POST['userId']))
    {
        $db = new DbOps();

        $result = $db->deleteUser($_POST['userId']);
        if($result == 0)
        {

            $result = $db->deletePictureFromAuthor($_POST['userId']);
            if ($result == 0)
            {
              $response['error'] = false;
              $response['message'] = "User ".$_POST['userId']." was deleted successfully along with the pictures they posted";
            }else
            {
              $response['error'] = true;
              $response['message'] = "Account deleted, but could not delete the images posted by account";
            }


        }elseif($result == 1)
        {
            $response['error'] = true;
            $response['message'] = "Error deleting account";
        }elseif($result == 2)
        {
            $response['error'] = true;
            $response['message'] = "User does not exist!";
        }
    }else
    {
        $response['error'] = true;
        $response['message'] = "User Id field not present!";
    }
}else
{
    $response['error'] = true;
    $response['message'] = "Invalid Request!";
}

echo json_encode($response);
?>
