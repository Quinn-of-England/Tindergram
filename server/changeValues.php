<?php

    include_once "DbOps.php";

    $response = array();

    if($_SERVER['REQUEST_METHOD']=='POST'){
        if((isset($_POST['username']) and isset($_POST['password']) and isset($_POST['id'])) or
           (isset($_POST['username']) and isset($_POST['email']) and isset($_POST['id'])) or 
           (isset($_POST['password']) and isset($_POST['email']) and isset($_POST['id']))){
            $response['error'] = true;
            $response['message'] = "Incorrect field selected";
           }else{
        if(isset($_POST['username']) and isset($_POST['id'])){
            $db = new DbOps();
            
            $result = $db->changeUsername($_POST['username'], $_POST['id']);
            if($result == 1){
                $response['error'] = false;
                $response['message'] = "Username successfully changed";
            }elseif($result == 2){
                $response['error'] = true;
                $response['message'] = "Error changing username";
            }elseif($result == 0){
                $response['error'] = true;
                $response['message'] = "Username is already used. Please provide a different username.";
            }
        }elseif(isset($_POST['password']) and isset($_POST['id'])){
            $db = new DbOps();
            
            if($db->changePassword($_POST['password'], $_POST['id'])){
                $response['error'] = false;
                $response['message'] = "Password successfully changed";
            }else{
                $response['error'] = true;
                $response['message'] = "Error changing password";
            }
        }elseif(isset($_POST['email']) and isset($_POST['id'])){
            $db = new DbOps();
            
            $result = $db->changeEmail($_POST['email'], $_POST['id']);
            if($result == 1){
                $response['error'] = false;
                $response['message'] = "Email successfully changed";
            }elseif($result == 2){
                $response['error'] = true;
                $response['message'] = "Error changing email address";
            }elseif($result == 0){
                $response['error'] = true;
                $response['message'] = "Email address provided is already used. Please provide a different email address.";
            }
        }else{
            $response['error'] = true;
            $response['message'] = "Incorrect field selected";
        }
    }
    }else{
        $response['error'] = true;
        $response['message'] = "Invalid Request";
    }

    echo json_encode($response);
?>

