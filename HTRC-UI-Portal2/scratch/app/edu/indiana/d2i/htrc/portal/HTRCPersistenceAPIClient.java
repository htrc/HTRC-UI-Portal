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

package edu.indiana.d2i.htrc.portal;

import models.Workset;
import org.apache.amber.oauth2.client.OAuthClient;
import org.apache.amber.oauth2.client.URLConnectionClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.response.OAuthClientResponse;
import org.apache.amber.oauth2.common.message.types.GrantType;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import play.Logger;
import registry.workset.WorksetContent;
import registry.workset.WorksetMeta;
import registry.workset.Worksets;

import javax.xml.bind.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HTRCPersistenceAPIClient {
    private static Logger.ALogger log = play.Logger.of("application");

    private String accessToken;
    private String refreshToken;
    private Map<String, Object> session;
    private HttpClient client = new HttpClient();

    public int responseCode;

    /**
     * different service urls
     */
    private final String WORKSETS_URL = "/worksets";
    private final String FILES_URL = "/files";
    private final String PUBLIC_WORKSET = "?public=true";
    private final String VOLUME_DETAILS_QUERY_SOLR_URL = "?q=id:";

    /**
     * access token renew time
     */
    private int renew = 0;
    private final int MAX_RENEW = 1;


    private static class RSLValidationEventHandler implements
            ValidationEventHandler {
        public boolean handleEvent(ValidationEvent ve) {
            if (ve.getSeverity() == ValidationEvent.FATAL_ERROR
                    || ve.getSeverity() == ValidationEvent.ERROR)
                return false;
            return true;
        }
    }

    private Object parseXML(String xmlStr) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext
                .newInstance("registry.workset");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setEventHandler(new RSLValidationEventHandler());
        JAXBElement<Object> worksets = (JAXBElement<Object>) unmarshaller
                .unmarshal(new ByteArrayInputStream(xmlStr.getBytes()));
        return worksets.getValue();
    }


    /**
     * This method renew the access token given a refresh token.
     *
     * @param refreshToken
     * @return access token
     * @throws IOException
     * @throws org.apache.amber.oauth2.common.exception.OAuthSystemException
     * @throws org.apache.amber.oauth2.common.exception.OAuthProblemException
     * @throws Exception
     */
    public static String renewToken(String refreshToken)
            throws Exception {
        OAuthClientRequest refreshTokenRequest = OAuthClientRequest
                .tokenLocation(PlayConfWrapper.tokenEndpoint())
                .setGrantType(GrantType.REFRESH_TOKEN).setRefreshToken(refreshToken)
                .setClientId(PlayConfWrapper.oauthClientID())
                .setClientSecret(PlayConfWrapper.oauthClientSecrete())
                .buildBodyMessage();
        OAuthClient refreshTokenClient = new OAuthClient(new URLConnectionClient());
        OAuthClientResponse refreshTokenResponse = refreshTokenClient
                .accessToken(refreshTokenRequest);
        String refreshAccessToken = refreshTokenResponse.getParam("access_token");
        log.info("Access token has been renewed to " + refreshAccessToken);

        return refreshAccessToken;
    }

    public HTRCPersistenceAPIClient(String accessToken) {
        this.accessToken = accessToken;

    }



    public List<Workset> getAllWorksets() throws IOException,
            JAXBException {
        String url = PlayConfWrapper.registryEPR() + WORKSETS_URL +
                PUBLIC_WORKSET;
        log.debug("getAllWorksets url: " + url);

        GetMethod get = new GetMethod(url);
        get.addRequestHeader("Authorization", "Bearer " + accessToken);
        get.addRequestHeader("Accept", "application/vnd.htrc-workset+xml");

        int response = client.executeMethod(get);
        this.responseCode = response;
        if (response == 200) {
            String xmlStr = get.getResponseBodyAsString();
            System.out.println(xmlStr);
            Worksets worksets = (Worksets) parseXML(xmlStr);

            Map<String, Workset> userWorksets = new TreeMap<String, Workset>();
            List<Workset> publicWorksets = new ArrayList<Workset>();
            for (registry.workset.Workset workset : worksets.getWorkset()) {

                WorksetMeta metadata = workset.getMetadata();
                WorksetContent worksetContent = workset.getContent();

                Calendar calendar = metadata.getLastModified().toGregorianCalendar();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                formatter.setTimeZone(calendar.getTimeZone());
                String dateString = formatter.format(calendar.getTime());
                // mandatory options
                Workset ws = new Workset(
                        metadata.getName(),
                        metadata.getDescription(),
                        metadata.getAuthor(),
                        metadata.getLastModifiedBy(),
                        dateString,
                        metadata.getVolumeCount(),
                        true
                );




                publicWorksets.add(ws);



//                //WorksetMeta metadata = workset.getMetadata();
//                Workset ws = createWorksetBean(workset, 0);
//                if (ws.getAuthor().equalsIgnoreCase(userName)) {
//                    userWorksets.put(ws.getUniqueIdentity(), ws);
//                } else {
//                    otherWorksets.put(ws.getUniqueIdentity(), ws);
//                }
            }

//            Map<String, Workset> res = new LinkedHashMap<String, Workset>();
//            for (Map.Entry<String, Workset> entry : userWorksets.entrySet()) {
//                res.put(entry.getKey(), entry.getValue());
//            }
//            for (Map.Entry<String, Workset> entry : publicWorksets.entrySet()) {
//                res.put(entry.getKey(), entry.getValue());
//            }
//            renew = 0;
            return publicWorksets;
        } else {
            return null;
    }
    }

}
