/**
 * Copyright 2013 The Trustees of Indiana University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express  or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package models;

import edu.indiana.d2i.htrc.portal.PlayConfWrapper;
import htrc.security.oauth2.client.OAuth2Client;
import htrc.security.oauth2.client.OAuthUserInfoRequest;
import org.apache.amber.oauth2.client.URLConnectionClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.response.OAuthClientResponse;
import org.apache.amber.oauth2.common.message.types.GrantType;
import play.Logger;
import play.Play;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User extends Model {

    @Id
    public String userId;
    public String email;
    public String password;
    public String accessToken;

    private static Logger.ALogger log = play.Logger.of("application");

    public User(String userId, String email, String password, String accessToken) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.accessToken = accessToken;
    }

    public static Finder<String, User> find = new Finder<String, User>(
            String.class, User.class
    );


    public static User authenticate(String userId, String password) {




        try {
            OAuthClientRequest accessTokenRequest = OAuthClientRequest
                    .tokenLocation(Play.application().configuration().getString("oauth2.token.endpoint"))
                    .setGrantType(GrantType.PASSWORD)
                    .setClientId(PlayConfWrapper.oauthClientID())
                    .setClientSecret(PlayConfWrapper.oauthClientSecrete())
                    .setUsername(userId)
                    .setPassword(password)
                    .buildBodyMessage();

            OAuth2Client accessTokenClient = new OAuth2Client(new URLConnectionClient());
            OAuthClientResponse accessTokenResponse = accessTokenClient.accessToken(accessTokenRequest);

            String accessToken = accessTokenResponse.getParam("access_token");
            User u = find.where().eq("userId", userId).findUnique();
            if(u == null){
                OAuthClientRequest userInfoRequest = OAuthUserInfoRequest
                        .userInfoLocation(Play.application().configuration().getString("oauth2.userinfo.endpoint"))
                        .setClientId(Play.application().configuration().getString("oauth2.client.id"))
                        .setClientSecret(Play.application().configuration().getString("oauth2.client.secrete"))
                        .setAccessToken(accessToken)
                        .buildBodyMessage();

                OAuth2Client userInfoClient = new OAuth2Client(new URLConnectionClient());
                OAuthClientResponse userInfoResponse = userInfoClient.userInfo(userInfoRequest);
                String email = userInfoResponse.getParam("user_email");
                User nu = new User(userId,email,password,accessToken);
                nu.save();
                return nu;

            } else{
                u.accessToken = accessToken;
                u.save();
                return u;
            }
        } catch (Exception e) {
            log.error("Invalid user ID or Password", e);
            return null;
        }
//        return find.where().eq("userId",userId).eq("password",password).findUnique();
        //To change body of created methods use File | Settings | File Templates.
    }
}