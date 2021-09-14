
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.PivotTable;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public class Teste {
    private static Sheets sheetsService;
    private static String APPLICATION_NAME = "Teste";
    private static String SPREADSHEET_ID = "1JlgJqvtU_cw1hnMsmGyVoCNbktzSWUj26wAckMYCVV8";
    private static Integer NUMERO_TOTAL_AULAS=60;

    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = Teste.class.getResourceAsStream("/credentials.json");

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
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                credential).setApplicationName(APPLICATION_NAME).build();
    }

    private static List<List<Object>> readData()throws IOException, GeneralSecurityException{
        sheetsService = getSheetService();
        String range = "engenharia_de_software!A4:H27";
        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        return values;
    }

    private static void atualizaSituacao(String situacao,String linha)throws IOException, GeneralSecurityException{
        List<List<Object>> newValues = Arrays.asList(Arrays.asList(situacao));
        ValueRange body = new ValueRange().setValues(newValues);

        UpdateValuesResponse result = sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, "engenharia_de_software!G"+linha, body).setValueInputOption("RAW").execute();

    }

    private static void atualizaNotaAprovacao(float nota,String linha)throws IOException, GeneralSecurityException{
        List<List<Object>> newValues = Arrays.asList(Arrays.asList(nota));
        ValueRange body = new ValueRange().setValues(newValues);

        UpdateValuesResponse result = sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, "engenharia_de_software!H"+linha, body).setValueInputOption("RAW").execute();
    }

    private static float calculoFaltas(float numeroFaltas){
        // System.out.printf("\n Porcentagem faltas: %f",(50/100));
        float porcentgem=(numeroFaltas/NUMERO_TOTAL_AULAS)*100;
        return porcentgem;
    }
    private static float calculoMedia(int nota1,int nota2,int nota3){
        float media=(nota1+nota2+nota3)/3;
        return media;
    }
    private static int calculaNAF(int media){
        int naf=100-media;
        return naf;
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {

        List<List<Object>> values=readData();
        if (values == null || values.isEmpty()) {
            System.out.println("Não foram encontrado dados");
        } else {
            values.parallelStream().forEach(row->{
                System.out.printf("\n Id usuario: %s", row.get(0));
                int idLinha=Integer.parseInt((String)row.get(0))+3;
                String linha=Integer.toString((int)idLinha);
                float porcentagemFaltas=calculoFaltas(Float.parseFloat((String) row.get(2)));
                if (porcentagemFaltas>(25)) {
                    try {
                        atualizaSituacao("Reprovado Por falta",linha);
                        atualizaNotaAprovacao(0,linha);   
                    } catch (Exception e) {
                        System.out.printf("Ocorreu um problema por favor tente mais tarde", e);
                    }
                }
                else{
                    int media=(int)Math.ceil(
                        calculoMedia(Integer.parseInt((String) row.get(3)),
                            Integer.parseInt((String) row.get(4)),
                            Integer.parseInt((String) row.get(5))
                        )
                    );
                    if (media>=70) {
                        try {
                            atualizaSituacao("Aprovado",linha);
                            atualizaNotaAprovacao(0,linha);
                        } catch (Exception e) {
                            System.out.printf("Ocorreu um problema por favor tente mais tarde", e);
                        }
                    } else if(media<5) {
                        try {
                            atualizaSituacao("Reprovado Por Nota",linha);
                            atualizaNotaAprovacao(0,linha);  
                        } catch (Exception e) {
                            System.out.printf("Ocorreu um problema por favor tente mais tarde", e);
                        }
                    } else{
                        try {
                            int notaFinalAprovacao=calculaNAF(media);
                            atualizaSituacao("Exame Final",linha);
                            atualizaNotaAprovacao(notaFinalAprovacao,linha);
                        } catch (Exception e) {
                            System.out.printf("Ocorreu um problema por favor tente mais tarde", e);
                        }

                    }

                }
            });
            // for(List row : values) {
            //     int idLinha=Integer.parseInt((String)row.get(0))+3;
            //     String linha=Integer.toString((int)idLinha);
            //     float porcentagemFaltas=calculoFaltas(Float.parseFloat((String) row.get(2)));
            //     if (porcentagemFaltas>(25)) {
            //         atualizaSituacao("Reprovado Por falta",linha);
            //         atualizaNotaAprovacao(0,linha);
            //     }
            //     else{
            //         int media=(int)Math.ceil(
            //             calculoMedia(Integer.parseInt((String) row.get(3)),
            //                 Integer.parseInt((String) row.get(4)),
            //                 Integer.parseInt((String) row.get(5))
            //             )
            //         );
            //         if (media>=70) {
            //             atualizaSituacao("Aprovado",linha);
            //             atualizaNotaAprovacao(0,linha);
            //         } else if(media<5) {
            //             atualizaSituacao("Reprovado Por Nota",linha);
            //             atualizaNotaAprovacao(0,linha);
            //         } else{
            //             int notaFinalAprovacao=calculaNAF(media);
            //             atualizaSituacao("Exame Final",linha);
            //             atualizaNotaAprovacao(notaFinalAprovacao,linha);
            //         }

            //     }
            // }
        // values.parallelStream()
        //     .forEach(row->System.out.printf("\n Id usuario: %s", row.get(0)));
        //}
            }
    }

}
