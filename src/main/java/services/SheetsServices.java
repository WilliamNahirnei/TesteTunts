package services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

public class SheetsServices {
    private static Sheets sheets;
    private static String APPLICATION_NAME = "Notas_Alunos";
    private static String SPREADSHEET_ID = "1JlgJqvtU_cw1hnMsmGyVoCNbktzSWUj26wAckMYCVV8";

    private static Credential authorize() throws IOException, GeneralSecurityException {
        System.out.println("Iniciando Processo de autenticação");
        InputStream in = SheetsServices.class.getResourceAsStream("/credentials.json");

        GoogleClientSecrets clienteSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
                new InputStreamReader(in));
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), clienteSecrets,
                scopes).setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                        .setAccessType("offline").build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        return credential;

    }

    public static Sheets getSheetService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();
        System.out.println("Cronstruindo credenciais");
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                credential).setApplicationName(APPLICATION_NAME).build();
    }

    public static List<List<Object>> getData() throws IOException, GeneralSecurityException {
        sheets = getSheetService();
        System.out.println("Estabelencendo conexão com a planilha e lendo dados");
        String range = "engenharia_de_software!A4:H27";
        ValueRange response = sheets.spreadsheets().values().get(SPREADSHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        return values;
    }

    public static <T> void updateData(T value, T lineInSheets,T column) {
        List<List<Object>> newValues = Arrays.asList(Arrays.asList(value));
        ValueRange data = new ValueRange().setValues(newValues);

        try {
            sheets.spreadsheets().values()
                    .update(SPREADSHEET_ID, "engenharia_de_software!" +column+ lineInSheets, data).setValueInputOption("RAW")
                    .execute();
        } catch (IOException e) {
            System.out.printf("an error occurred while modifying data in worksheet, insert data at time of error: {value:%s,worksheet_row:%s}", value,lineInSheets);
            e.printStackTrace();
        }
    }

    // private static void updateSituation(String situation,String line)throws
    // IOException, GeneralSecurityException{
    // List<List<Object>> newValues = Arrays.asList(Arrays.asList(situation));
    // ValueRange data = new ValueRange().setValues(newValues);

    // UpdateValuesResponse result = sheets.spreadsheets().values()
    // .update(SPREADSHEET_ID, "engenharia_de_software!G"+line,
    // data).setValueInputOption("RAW").execute();

    // }

    // private static void updateNote(float note,String line)throws IOException,
    // GeneralSecurityException{
    // List<List<Object>> newValues = Arrays.asList(Arrays.asList(note));
    // ValueRange data = new ValueRange().setValues(newValues);

    // UpdateValuesResponse result = sheets.spreadsheets().values()
    // .update(SPREADSHEET_ID, "engenharia_de_software!H"+line,
    // data).setValueInputOption("RAW").execute();
    // }

}
