/*******************************************************************************
 * Copyright 2012 , http://www.prosyst.com - ProSyst Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.universAAL.AALapplication.medication_manager.user.management.impl.insert.dummy.users;

import org.universAAL.AALapplication.medication_manager.user.management.impl.MedicationManagerUserManagementException;
import org.universAAL.ontology.vcard.Cell;
import org.universAAL.ontology.vcard.Email;
import org.universAAL.ontology.vcard.Fax;
import org.universAAL.ontology.vcard.Internet;
import org.universAAL.ontology.vcard.Msg;
import org.universAAL.ontology.vcard.Tel;
import org.universAAL.ontology.vcard.Video;
import org.universAAL.ontology.vcard.Voice;
import org.universAAL.ontology.vcard.X400;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.universAAL.AALapplication.medication_manager.user.management.impl.insert.dummy.users.TelEnum.*;


/**
 * @author George Fournadjiev
 */
public final class VCardBuilder {

  private String personUri;
  private String vcardVersion;
  private Date lastRevision;
  private String nickname;
  private String displayName;
  private String uciLabel;
  private String uciAdditionalData;
  private String aboutMe;
  private Date bday;
  private String fn;
  private List<Telephone> telephones;
  private List<Mail> emails;

  public void buildPersonUri(String personUri) {
    this.personUri = personUri;
  }

  public void buildVcardVersion(String vCardVersion) {
    this.vcardVersion = vCardVersion;
  }

  public void buildLastRevision(Date lastRevision) {
    this.lastRevision = lastRevision;
  }

  public void buildNickname(String nickname) {
    this.nickname = nickname;
  }

  public void buildDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void buildUciLabel(String uciLabel) {
    this.uciLabel = uciLabel;
  }

  public void buildUciAdditional_data(String uciAdditionalData) {
    this.uciAdditionalData = uciAdditionalData;
  }

  public void buildAboutMe(String aboutMe) {
    this.aboutMe = aboutMe;
  }

  public void buildBday(Date bday) {
    this.bday = bday;
  }

  public void buildFn(String fn) {
    this.fn = fn;
  }

  public void buildTelephones(List<Tel> telephones) {
    this.telephones = getTelephones(telephones);
  }

  public void buildEmails(List<Email> emails) {
    this.emails = getEmails(emails);
  }

  private List<Telephone> getTelephones(List<Tel> telList) {
    if (telList == null || telList.isEmpty()) {
      return new ArrayList<Telephone>();
    }

    List<Telephone> telephoneList = new ArrayList<Telephone>();

    for (Tel tel : telList) {
      Telephone telephone = createTelephone(tel);
      telephoneList.add(telephone);
    }

    return telephoneList;
  }

  private Telephone createTelephone(Tel tel) {

    TelEnum telEnum;

    if (tel instanceof Cell) {
      telEnum = CELL;
    } else if (tel instanceof Voice) {
      telEnum = VOICE;
    } else if (tel instanceof Msg) {
      telEnum = MSG;
    } else if (tel instanceof Video) {
      telEnum = VIDEO;
    } else if (tel instanceof Fax) {
      telEnum = FAX;
    } else {
      throw new MedicationManagerUserManagementException("Unsupported version of the Tel subclass : " + tel.getClass());
    }

    String value = (String) tel.getProperty(Tel.PROP_VALUE);
    return new Telephone(value, telEnum);
  }

  private List<Mail> getEmails(List<Email> mails) {
    if (mails == null || mails.isEmpty()) {
      return new ArrayList<Mail>();
    }

    List<Mail> emailList = new ArrayList<Mail>();

    for (Email mail : mails) {
      Mail em = createEmail(mail);
      emailList.add(em);
    }

    return emailList;
  }

  private Mail createEmail(Email mail) {

    EmailEnum emailEnum;

    if (mail instanceof Internet) {
      emailEnum = EmailEnum.INTERNET;
    } else if (mail instanceof X400) {
      emailEnum = EmailEnum.X400;
    } else {
      throw new MedicationManagerUserManagementException("Unsupported version of the Email subclass : " + mail.getClass());
    }

    String value = (String) mail.getProperty(Email.PROP_VALUE);
    return new Mail(value, emailEnum);
  }

  public VCard buildVCard() {
    VCard vCard = new VCard(
        personUri,
        vcardVersion,
        lastRevision,
        nickname,
        displayName,
        uciLabel,
        uciAdditionalData,
        aboutMe,
        bday,
        fn,
        telephones,
        emails
    );


    return vCard;
  }
}
