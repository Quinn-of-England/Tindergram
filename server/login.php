<?php

include_once "DbOps.php";

$response = array();

if($_SERVER['REQUEST_METHOD']=='POST')
{

    if(isset($_POST['username']) and isset($_POST['password']))
    {
        $db = new DbOps();

        if($db->loginUser($_POST['username'], $_POST['password']))
        {
            $user = $db->getUserByUsername($_POST['username']);
            $response['error'] = false;
            $response['message'] = "Login successful";
            $response['id'] = $user['id'];
            $response['email'] = $user['email'];
            $response['username'] = $user['username'];
        }else
        {
            $response['error'] = true;
            $response['message'] = "The provided username or password is incorrect. Please check and try again.";
        }
    }else
    {
        $response['error'] = true;
        $response['message'] = "Field not present";
    }
}else
{
    $response['error'] = true;
    $response['message'] = "Invalid Request";
}
echo json_encode($response);
?>
