1. download libreOffice from https://www.libreoffice.org/

2. find soffice.exe from ./program/ folder, where you set-up libreOffice

3. Convert from word to ODT
./soffice.exe --convert-to odt "path_to_doc_or_docx_file" --optdir "path_for_odt_to_output"


4. Convert from excel to ODS
./soffice.exe --convert-to ods "path_to_xls_or_xlsx_file" --optdir "path_for_ods_to_output"