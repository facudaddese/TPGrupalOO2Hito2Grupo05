package com.unla.grupo5.services.impl;

import com.unla.grupo5.dtos.LiquidacionHaberesDTO;
import com.unla.grupo5.entities.Festival;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.services.ReciboSueldoPdfService;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Font;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class IReciboSueldoPdfService implements ReciboSueldoPdfService {

    public byte[] generarRecibo(LiquidacionHaberesDTO liquidacion, Festival festival, UnidadDeVenta unidad) throws DocumentException {

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document();

        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            Font titulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font subtitulo = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font texto = new Font(Font.HELVETICA, 11, Font.NORMAL);
            Font total = new Font(Font.HELVETICA, 14, Font.BOLD);

            NumberFormat moneda =
                    NumberFormat.getCurrencyInstance(
                            Locale.forLanguageTag("es-AR"));

            documento.add(new Paragraph(
                    "EPICENTRO GOURMET", titulo));

            documento.add(new Paragraph(
                    "RECIBO DE SUELDO", subtitulo));

            documento.add(new Paragraph(" "));

            documento.add(new Paragraph(
                    "DATOS DEL EMPLEADO", subtitulo));

            documento.add(new Paragraph(
                    "Nombre: " + liquidacion.getNombreCompleto(),
                    texto));

            documento.add(new Paragraph(
                    "Tipo de empleado: " + liquidacion.getTipoEmpleado(),
                    texto));

            documento.add(new Paragraph(" "));

            documento.add(new Paragraph(
                    "DATOS DEL FESTIVAL", subtitulo));

            documento.add(new Paragraph(
                    "Festival: " + festival.getNombre(), texto));

            documento.add(new Paragraph(
                    "Unidad de venta: " + unidad.getNombreComercial(),
                    texto));

            documento.add(new Paragraph(
                    "Período liquidado: "
                            + liquidacion.getPeriodoInicio()
                            + " al "
                            + liquidacion.getPeriodoFin(),
                    texto));

            documento.add(new Paragraph(
                    "Días liquidados: "
                            + liquidacion.getDiasLiquidacion(),
                    texto));

            documento.add(new Paragraph(" "));

            documento.add(new Paragraph(
                    "DETALLE DE HABERES", subtitulo));

            documento.add(new Paragraph(
                    "Sueldo base: "
                            + moneda.format(liquidacion.getSueldoBase()),
                    texto));

            String concepto = liquidacion.getPlus();

            if (concepto == null || concepto.isBlank()) {
                concepto = "Sin adicional";
            }

            documento.add(new Paragraph(
                    "Concepto adicional: " + concepto, texto));

            documento.add(new Paragraph(
                    "Monto adicional: "
                            + moneda.format(liquidacion.getMontoAdicional()),
                    texto));

            documento.add(new Paragraph(
                    "Factor de duración: "
                            + liquidacion.getFactorDuracion(),
                    texto));

            documento.add(new Paragraph(" "));

            documento.add(new Paragraph(
                    "Cálculo: ("
                            + moneda.format(liquidacion.getSueldoBase())
                            + " + "
                            + moneda.format(liquidacion.getMontoAdicional())
                            + ") x "
                            + liquidacion.getFactorDuracion(),
                    texto));

            documento.add(new Paragraph(
                    "TOTAL DE HABERES: "
                            + moneda.format(liquidacion.getSueldoCalculado()),
                    total));

            documento.add(new Paragraph(" "));
            documento.add(new Paragraph(
                    "Recibo generado por el sistema Epicentro Gourmet.",
                    texto));

        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }

        return salida.toByteArray();
    }
}
