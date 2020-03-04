<?php

    include_once "DbOps.php";

    $response = array();

    if($_SERVER['REQUEST_METHOD']=='POST'){
        if(isset($_POST['username']) and isset($_POST['password']) and isset($_POST['email'])){
            $db = new DbOps();
            
            $result = $db->createUser($_POST['username'], $_POST['password'], $_POST['email']);
            if($result == 1){
                $user = $db->getUserByUsername($_POST['username']);
                $response['error'] = false;
                $response['message'] = "Registration successful";
                $response['id'] = $user['id'];
                $response['email'] = $user['email'];
                $response['username'] = $user['username'];
            }elseif($result == 2){
                $response['error'] = true;
                $response['message'] = "Error adding new account";
            }elseif($result == 0){
                $response['error'] = true;
                $response['message'] = "Username or email address provided is already used. Please provide a different username or email address or login using an existing account.";
            }
        }else{
            $response['error'] = true;
            $response['message'] = "Field not present";
        }
    }else{
        $response['error'] = true;
        $response['message'] = "Invalid Request";
    }

    echo json_encode($response);
?>