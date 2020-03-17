<?php
    include_once "DbConnect.php";

    class DbOps{
        public $conn;

        function __construct(){
            $db = new DbConnect();

            $this->conn = $db->connect();
        }

        public function createUser($username, $password, $email){
            if($this->isUserExist($username, $email)) {
                return 0;
            }else{
                $encrPassword = md5($password);
                $stmt = $this->conn->prepare("INSERT INTO `users`(`username`, `email`, `password`) VALUES (?,?,?);");
                $stmt->bind_param("sss", $username, $email, $encrPassword);

                if($stmt->execute()){
                    return 1;
                }else{
                    echo $this->conn->error;
                    return 2;
                }
            }
        }

        public function changeUsername($username, $id){
            if($this->isUsernameExist($username)) {
                return 0;
            }else{
                $stmt = $this->conn->prepare("UPDATE `users` SET `username` = ? WHERE `users`.`id` = ?");
                $stmt->bind_param("si", $username, $id);

                if($stmt->execute()){
                    return 1;
                }else{
                    echo $this->conn->error;
                    return 2;
                }
            }
        }

        public function changePassword($password, $id){
            $encrPassword = md5($password);
            $stmt = $this->conn->prepare("UPDATE `users` SET `password` = ? WHERE `users`.`id` = ?");
            $stmt->bind_param("si", $encrPassword, $id);

            if($stmt->execute()){
                return true;
            }else{
                echo $this->conn->error;
                return false;
            }
        }

        public function changeEmail($email, $id){
            if($this->isEmailExist($email)) {
                return 0;
            }else{
                $stmt = $this->conn->prepare("UPDATE `users` SET `email` = ? WHERE `users`.`id` = ?");
                $stmt->bind_param("si", $email, $id);

                if($stmt->execute()){
                    return 1;
                }else{
                    echo $this->conn->error;
                    return 2;
                }
            }
        }

        private function isUserExist($username, $email) {
            $stmt = $this->conn->prepare("SELECT id FROM users WHERE username = ? OR email = ?");
            $stmt->bind_param("ss", $username, $email);
            $stmt->execute();
            $stmt->store_result();
            return $stmt->num_rows > 0;
        }

        public function isUsernameExist($username) {
            $stmt = $this->conn->prepare("SELECT id FROM users WHERE username = ?");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            $stmt->store_result();
            return $stmt->num_rows > 0;
        }

        public function isEmailExist($email) {
            $stmt = $this->conn->prepare("SELECT id FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $stmt->store_result();
            return $stmt->num_rows > 0;
        }

        public function loginUser($username, $password) {
            $encrPassword = md5($password);
            $stmt = $this->conn->prepare("SELECT id FROM users WHERE username = ? AND password = ?");
            $stmt->bind_param("ss", $username, $encrPassword);
            $stmt->execute();
            $stmt->store_result();
            return $stmt->num_rows > 0;
        }

        public function getUserByUsername($username) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE username = ?");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            return $stmt->get_result()->fetch_assoc();
	      }

        public function getIdByUsername($username) {
            $stmt = $this->conn->prepare("SELECT id FROM users WHERE username = ?");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            return $stmt->get_result()->fetch_assoc();
	      }

        public function userAlreadyNotified($authorId, $followerId){
            $stmt = $this->conn->prepare("SELECT followedPostedPictures FROM users WHERE id = ?");
            $stmt->bind_param("s", $followerId);
            $stmt->execute();
            $stmt->store_result();
            $stmt->bind_result($followedString);
            $stmt->fetch();
            $notificationArr = explode(',', $followedString);
            foreach ($notificationArr as $notif) {
              if ($notif == $authorId) {
                return true;
              }
            }
            return false;
        }

        public function notifyAllThatFollowId($authorId){
            $stmt = $this->conn->prepare("SELECT isFollowedBy FROM users WHERE id = ?");
            $stmt->bind_param("s", $authorId);
            $stmt->execute();
            $stmt->store_result();
            $stmt->bind_result($followers);
    			  $stmt->fetch();
            $followersArr = explode(',', $followers);
            //for each follower in followers, change their update column
            foreach ($followersArr as $follower) {
              //notify the follower once.
              if (!$this->userAlreadyNotified($authorId, $follower)){
                $stmt = $this->conn->prepare("UPDATE users SET followedPostedPictures = CONCAT(followedPostedPictures,?) WHERE id = ?");
                $formattedAuthorId = $authorId.",";
                $stmt->bind_param("ss", $formattedAuthorId, $follower);
                $stmt->execute();
              }
            }
        }

        public function getNotification($userId){
            $stmt = $this->conn->prepare("SELECT followedPostedPictures FROM users WHERE id = ?");
            $stmt->bind_param("s", $userId);
            $stmt->execute();
            $stmt->store_result();
            $stmt->bind_result($followedUsers);
            $stmt->fetch();
            $followedArr = explode(',', $followedUsers);
            $resultList = "";
            //for each follower in followers, change their update column
            foreach ($followedArr as $followed) {
              $stmt = $this->conn->prepare("SELECT username FROM users WHERE id = ?");
              $stmt->bind_param("s", $followed);
              $stmt->execute();
              $stmt->store_result();
              $stmt->bind_result($authorUserName);
              $stmt->fetch();
              $resultList = $resultList.$authorUserName.",";
            }
            return $resultList;
        }

        public function canFollow($followerId,$followedId){
          $stmt = $this->conn->prepare("SELECT isFollowedBy FROM users WHERE id = ?");
          $stmt->bind_param("s", $followedId);
          $stmt->execute();
          $stmt->store_result();
          $stmt->bind_result($followers);
          $stmt->fetch();
          $followersArr = explode(',', $followers);
          foreach ($followersArr as $follower) {
            if($follower == $followerId){
              return false;
            }
          }
          return true;
        }

        public function follows($followerId,$followedId){
          //TODO checking if the user is already being followed, maybe that can be another method
          $stmt = $this->conn->prepare("UPDATE `users` SET `isFollowedBy` = CONCAT(`isFollowedBy`,?) WHERE `id` = ?");
          $formattedfollowerId = $followerId.",";
          $stmt->bind_param("ss", $formattedfollowerId, $followedId);
          return $stmt->execute();
        }

	      public function addComment($id,$comment,$username){
          //should pass the comment as $comment = "user,comment|"
          //storing old value and adding received param to it
          $toAdd = $username.",".$comment."|";
          $stmt = $this->conn->prepare("UPDATE `pictures` SET `comments` = CONCAT(`comments`,?) WHERE `id` = ?");
          $stmt->bind_param("ss", $toAdd, $id);
          return $stmt->execute();
        }
}
?>
