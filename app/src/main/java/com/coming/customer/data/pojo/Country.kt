package com.coming.customer.data.pojo

import android.content.Context
import android.text.TextUtils
import com.coming.customer.R
import java.util.*
import kotlin.Comparator

class Country {

    // region Variables
    private var code: String? = null
    var name: String? = null
    var dialCode: String? = null
    var flag: Int = 0
    // endregion

    // region Getter/Setter
    var currency: String? = null
    // endregion

    // region Constructors
    internal constructor() {}

    internal constructor(code: String, name: String, dialCode: String, flag: Int, currency: String) {
        this.code = code
        this.name = name
        this.dialCode = dialCode
        this.flag = flag
        this.currency = currency
    }

    fun getCode(): String? {
        return code
    }

    fun setCode(code: String) {
        this.code = code
        if (TextUtils.isEmpty(name)) {
            name = Locale("", code).displayName
        }
    }

    fun loadFlagByCode(context: Context) {
        if (this.flag != -1) {
            return
        }

        try {
            this.flag = context.resources
                .getIdentifier(
                    "flag_" + this.code!!.toLowerCase(Locale.ENGLISH), "drawable",
                    context.packageName
                )
        } catch (e: Exception) {
            e.printStackTrace()
            this.flag = -1
        }

    }

    companion object {
        private val COUNTRIES = arrayOf(
            Country("AD", "Andorra", "+376", R.drawable.flag_ad, "EUR"),
            Country("AE", "United Arab Emirates", "+971", R.drawable.flag_ae, "AED"),
            Country("AF", "Afghanistan", "+93", R.drawable.flag_af, "AFN"),
            Country("AG", "Antigua and Barbuda", "+1", R.drawable.flag_ag, "XCD"),
            Country("AI", "Anguilla", "+1", R.drawable.flag_ai, "XCD"),
            Country("AL", "Albania", "+355", R.drawable.flag_al, "ALL"),
            Country("AM", "Armenia", "+374", R.drawable.flag_am, "AMD"),
            Country("AO", "Angola", "+244", R.drawable.flag_ao, "AOA"),
            Country("AQ", "Antarctica", "+672", R.drawable.flag_aq, "USD"),
            Country("AR", "Argentina", "+54", R.drawable.flag_ar, "ARS"),
            Country("AS", "American Samoa", "+1", R.drawable.flag_as, "USD"),
            Country("AT", "Austria", "+43", R.drawable.flag_at, "EUR"),
            Country("AU", "Australia", "+61", R.drawable.flag_au, "AUD"),
            Country("AW", "Aruba", "+297", R.drawable.flag_aw, "AWG"),
            Country("AX", "Aland Islands", "+358", R.drawable.flag_ax, "EUR"),
            Country("AZ", "Azerbaijan", "+994", R.drawable.flag_az, "AZN"),
            Country("BA", "Bosnia and Herzegovina", "+387", R.drawable.flag_ba, "BAM"),
            Country("BB", "Barbados", "+1", R.drawable.flag_ba, "BBD"),
            Country("BD", "Bangladesh", "+880", R.drawable.flag_bd, "BDT"),
            Country("BE", "Belgium", "+32", R.drawable.flag_be, "EUR"),
            Country("BF", "Burkina Faso", "+226", R.drawable.flag_bf, "XOF"),
            Country("BG", "Bulgaria", "+359", R.drawable.flag_bg, "BGN"),
            Country("BH", "Bahrain", "+973", R.drawable.flag_bh, "BHD"),
            Country("BI", "Burundi", "+257", R.drawable.flag_bi, "BIF"),
            Country("BJ", "Benin", "+229", R.drawable.flag_bj, "XOF"),
            Country("BL", "Saint Barthelemy", "+590", R.drawable.flag_bl, "EUR"),
            Country("BM", "Bermuda", "+1", R.drawable.flag_bm, "BMD"),
            Country("BN", "Brunei Darussalam", "+673", R.drawable.flag_bn, "BND"),
            Country("BO", "Bolivia, Plurinational State of", "+591", R.drawable.flag_bo, "BOB"),
            Country("BQ", "Bonaire", "+599", R.drawable.flag_bq, "USD"),
            Country("BR", "Brazil", "+55", R.drawable.flag_br, "BRL"),
            Country("BS", "Bahamas", "+1", R.drawable.flag_bs, "BSD"),
            Country("BT", "Bhutan", "+975", R.drawable.flag_bt, "BTN"),
            Country("BV", "Bouvet Island", "+47", R.drawable.flag_bv, "NOK"),
            Country("BW", "Botswana", "+267", R.drawable.flag_bw, "BWP"),
            Country("BY", "Belarus", "+375", R.drawable.flag_by, "BYR"),
            Country("BZ", "Belize", "+501", R.drawable.flag_bz, "BZD"),
            Country("CA", "Canada", "+1", R.drawable.flag_cd, "CAD"),
            Country("CC", "Cocos (Keeling) Islands", "+61", R.drawable.flag_cc, "AUD"),
            Country("CD", "Congo, The Democratic Republic of the", "+243", R.drawable.flag_cd, "CDF"),
            Country("CF", "Central African Republic", "+236", R.drawable.flag_cf, "XAF"),
            Country("CG", "Congo", "+242", R.drawable.flag_cg, "XAF"),
            Country("CH", "Switzerland", "+41", R.drawable.flag_ch, "CHF"),
            Country("CI", "Ivory Coast", "+225", R.drawable.flag_ci, "XOF"),
            Country("CK", "Cook Islands", "+682", R.drawable.flag_ck, "NZD"),
            Country("CL", "Chile", "+56", R.drawable.flag_cl, "CLP"),
            Country("CM", "Cameroon", "+237", R.drawable.flag_cm, "XAF"),
            Country("CN", "China", "+86", R.drawable.flag_cn, "CNY"),
            Country("CO", "Colombia", "+57", R.drawable.flag_co, "COP"),
            Country("CR", "Costa Rica", "+506", R.drawable.flag_cr, "CRC"),
            Country("CU", "Cuba", "+53", R.drawable.flag_cu, "CUP"),
            Country("CV", "Cape Verde", "+238", R.drawable.flag_cv, "CVE"),
            Country("CW", "Curacao", "+599", R.drawable.flag_cw, "ANG"),
            Country("CX", "Christmas Island", "+61", R.drawable.flag_cx, "AUD"),
            Country("CY", "Cyprus", "+357", R.drawable.flag_cy, "EUR"),
            Country("CZ", "Czech Republic", "+420", R.drawable.flag_cz, "CZK"),
            Country("DE", "Germany", "+49", R.drawable.flag_de, "EUR"),
            Country("DJ", "Djibouti", "+253", R.drawable.flag_de, "DJF"),
            Country("DK", "Denmark", "+45", R.drawable.flag_dk, "DKK"),
            Country("DM", "Dominica", "+1", R.drawable.flag_dm, "XCD"),
            Country("DO", "Dominican Republic", "+1", R.drawable.flag_do, "DOP"),
            Country("DZ", "Algeria", "+213", R.drawable.flag_dz, "DZD"),
            Country("EC", "Ecuador", "+593", R.drawable.flag_ec, "USD"),
            Country("EE", "Estonia", "+372", R.drawable.flag_ee, "EUR"),
            Country("EG", "Egypt", "+20", R.drawable.flag_eg, "EGP"),
            Country("EH", "Western Sahara", "+212", R.drawable.flag_eh, "MAD"),
            Country("ER", "Eritrea", "+291", R.drawable.flag_er, "ERN"),
            Country("ES", "Spain", "+34", R.drawable.flag_es, "EUR"),
            Country("ET", "Ethiopia", "+251", R.drawable.flag_et, "ETB"),
            Country("FI", "Finland", "+358", R.drawable.flag_fi, "EUR"),
            Country("FJ", "Fiji", "+679", R.drawable.flag_fj, "FJD"),
            Country("FK", "Falkland Islands (Malvinas)", "+500", R.drawable.flag_fk, "FKP"),
            Country("FM", "Micronesia, Federated States of", "+691", R.drawable.flag_fm, "USD"),
            Country("FO", "Faroe Islands", "+298", R.drawable.flag_fo, "DKK"),
            Country("FR", "France", "+33", R.drawable.flag_fr, "EUR"),
            Country("GA", "Gabon", "+241", R.drawable.flag_ga, "XAF"),
            Country("GB", "United Kingdom", "+44", R.drawable.flag_gf, "GBP"),
            Country("GD", "Grenada", "+1", R.drawable.flag_gd, "XCD"),
            Country("GE", "Georgia", "+995", R.drawable.flag_ge, "GEL"),
            Country("GF", "French Guiana", "+594", R.drawable.flag_gf, "EUR"),
            Country("GG", "Guernsey", "+44", R.drawable.flag_gg, "GGP"),
            Country("GH", "Ghana", "+233", R.drawable.flag_gh, "GHS"),
            Country("GI", "Gibraltar", "+350", R.drawable.flag_gi, "GIP"),
            Country("GL", "Greenland", "+299", R.drawable.flag_gl, "DKK"),
            Country("GM", "Gambia", "+220", R.drawable.flag_gm, "GMD"),
            Country("GN", "Guinea", "+224", R.drawable.flag_gn, "GNF"),
            Country("GP", "Guadeloupe", "+590", R.drawable.flag_gp, "EUR"),
            Country("GQ", "Equatorial Guinea", "+240", R.drawable.flag_gq, "XAF"),
            Country("GR", "Greece", "+30", R.drawable.flag_gr, "EUR"),
            Country(
                "GS", "South Georgia and the South Sandwich Islands", "+500", R.drawable.flag_gs,
                "GBP"
            ),
            Country("GT", "Guatemala", "+502", R.drawable.flag_gt, "GTQ"),
            Country("GU", "Guam", "+1", R.drawable.flag_gu, "USD"),
            Country("GW", "Guinea-Bissau", "+245", R.drawable.flag_gw, "XOF"),
            Country("GY", "Guyana", "+595", R.drawable.flag_gy, "GYD"),
            Country("HK", "Hong Kong", "+852", R.drawable.flag_hk, "HKD"),
            Country("HM", "Heard Island and McDonald Islands", "+000", R.drawable.flag_hm, "AUD"),
            Country("HN", "Honduras", "+504", R.drawable.flag_hk, "HNL"),
            Country("HR", "Croatia", "+385", R.drawable.flag_hr, "HRK"),
            Country("HT", "Haiti", "+509", R.drawable.flag_ht, "HTG"),
            Country("HU", "Hungary", "+36", R.drawable.flag_hu, "HUF"),
            Country("ID", "Indonesia", "+62", R.drawable.flag_id, "IDR"),
            Country("IE", "Ireland", "+353", R.drawable.flag_ie, "EUR"),
            Country("IL", "Israel", "+972", R.drawable.flag_il, "ILS"),
            Country("IM", "Isle of Man", "+44", R.drawable.flag_im, "GBP"),
            Country("IN", "India", "+91", R.drawable.flag_in, "INR"),
            Country("IO", "British Indian Ocean Territory", "+246", R.drawable.flag_io, "USD"),
            Country("IQ", "Iraq", "+964", R.drawable.flag_iq, "IQD"),
            Country("IR", "Iran, Islamic Republic of", "+98", R.drawable.flag_ir, "IRR"),
            Country("IS", "Iceland", "+354", R.drawable.flag_is, "ISK"),
            Country("IT", "Italy", "+39", R.drawable.flag_it, "EUR"),
            Country("JE", "Jersey", "+44", R.drawable.flag_je, "JEP"),
            Country("JM", "Jamaica", "+1", R.drawable.flag_jm, "JMD"),
            Country("JO", "Jordan", "+962", R.drawable.flag_jo, "JOD"),
            Country("JP", "Japan", "+81", R.drawable.flag_jp, "JPY"),
            Country("KE", "Kenya", "+254", R.drawable.flag_ke, "KES"),
            Country("KG", "Kyrgyzstan", "+996", R.drawable.flag_kg, "KGS"),
            Country("KH", "Cambodia", "+855", R.drawable.flag_tc, "KHR"),
            Country("KI", "Kiribati", "+686", R.drawable.flag_ki, "AUD"),
            Country("KM", "Comoros", "+269", R.drawable.flag_km, "KMF"),
            Country("KN", "Saint Kitts and Nevis", "+1", R.drawable.flag_kn, "XCD"),
            Country("KP", "North Korea", "+850", R.drawable.flag_kp, "KPW"),
            Country("KR", "South Korea", "+82", R.drawable.flag_kr, "KRW"),
            Country("KW", "Kuwait", "+965", R.drawable.flag_kw, "KWD"),
            Country("KY", "Cayman Islands", "+345", R.drawable.flag_ky, "KYD"),
            Country("KZ", "Kazakhstan", "+7", R.drawable.flag_kz, "KZT"),
            Country("LA", "Lao People's Democratic Republic", "+856", R.drawable.flag_la, "LAK"),
            Country("LB", "Lebanon", "+961", R.drawable.flag_lb, "LBP"),
            Country("LC", "Saint Lucia", "+1", R.drawable.flag_lc, "XCD"),
            Country("LI", "Liechtenstein", "+423", R.drawable.flag_li, "CHF"),
            Country("LK", "Sri Lanka", "+94", R.drawable.flag_lk, "LKR"),
            Country("LR", "Liberia", "+231", R.drawable.flag_lr, "LRD"),
            Country("LS", "Lesotho", "+266", R.drawable.flag_ls, "LSL"),
            Country("LT", "Lithuania", "+370", R.drawable.flag_lt, "LTL"),
            Country("LU", "Luxembourg", "+352", R.drawable.flag_lu, "EUR"),
            Country("LV", "Latvia", "+371", R.drawable.flag_lv, "LVL"),
            Country("LY", "Libyan Arab Jamahiriya", "+218", R.drawable.flag_ly, "LYD"),
            Country("MA", "Morocco", "+212", R.drawable.flag_mc, "MAD"),
            Country("MC", "Monaco", "+377", R.drawable.flag_mc, "EUR"),
            Country("MD", "Moldova, Republic of", "+373", R.drawable.flag_md, "MDL"),
            Country("ME", "Montenegro", "+382", R.drawable.flag_me, "EUR"),
            Country("MF", "Saint Martin", "+590", R.drawable.flag_mf, "EUR"),
            Country("MG", "Madagascar", "+261", R.drawable.flag_mg, "MGA"),
            Country("MH", "Marshall Islands", "+692", R.drawable.flag_mh, "USD"),
            Country(
                "MK", "Macedonia, The Former Yugoslav Republic of", "+389", R.drawable.flag_mk,
                "MKD"
            ),
            Country("ML", "Mali", "+223", R.drawable.flag_ml, "XOF"),
            Country("MM", "Myanmar", "+95", R.drawable.flag_mm, "MMK"),
            Country("MN", "Mongolia", "+976", R.drawable.flag_mn, "MNT"),
            Country("MO", "Macao", "+853", R.drawable.flag_mo, "MOP"),
            Country("MP", "Northern Mariana Islands", "+1", R.drawable.flag_mp, "USD"),
            Country("MQ", "Martinique", "+596", R.drawable.flag_mq, "EUR"),
            Country("MR", "Mauritania", "+222", R.drawable.flag_mr, "MRO"),
            Country("MS", "Montserrat", "+1", R.drawable.flag_ms, "XCD"),
            Country("MT", "Malta", "+356", R.drawable.flag_mt, "EUR"),
            Country("MU", "Mauritius", "+230", R.drawable.flag_mu, "MUR"),
            Country("MV", "Maldives", "+960", R.drawable.flag_mv, "MVR"),
            Country("MW", "Malawi", "+265", R.drawable.flag_mw, "MWK"),
            Country("MX", "Mexico", "+52", R.drawable.flag_mx, "MXN"),
            Country("MY", "Malaysia", "+60", R.drawable.flag_my, "MYR"),
            Country("MZ", "Mozambique", "+258", R.drawable.flag_mz, "MZN"),
            Country("NA", "Namibia", "+264", R.drawable.flag_na, "NAD"),
            Country("NC", "New Caledonia", "+687", R.drawable.flag_nc, "XPF"),
            Country("NE", "Niger", "+227", R.drawable.flag_ne, "XOF"),
            Country("NF", "Norfolk Island", "+672", R.drawable.flag_nf, "AUD"),
            Country("NG", "Nigeria", "+234", R.drawable.flag_ng, "NGN"),
            Country("NI", "Nicaragua", "+505", R.drawable.flag_ni, "NIO"),
            Country("NL", "Netherlands", "+31", R.drawable.flag_nl, "EUR"),
            Country("NO", "Norway", "+47", R.drawable.flag_no, "NOK"),
            Country("NP", "Nepal", "+977", R.drawable.flag_np, "NPR"),
            Country("NR", "Nauru", "+674", R.drawable.flag_nr, "AUD"),
            Country("NU", "Niue", "+683", R.drawable.flag_nu, "NZD"),
            Country("NZ", "New Zealand", "+64", R.drawable.flag_nz, "NZD"),
            Country("OM", "Oman", "+968", R.drawable.flag_om, "OMR"),
            Country("PA", "Panama", "+507", R.drawable.flag_pa, "PAB"),
            Country("PE", "Peru", "+51", R.drawable.flag_pe, "PEN"),
            Country("PF", "French Polynesia", "+689", R.drawable.flag_pf, "XPF"),
            Country("PG", "Papua New Guinea", "+675", R.drawable.flag_pg, "PGK"),
            Country("PH", "Philippines", "+63", R.drawable.flag_ph, "PHP"),
            Country("PK", "Pakistan", "+92", R.drawable.flag_pk, "PKR"),
            Country("PL", "Poland", "+48", R.drawable.flag_pl, "PLN"),
            Country("PM", "Saint Pierre and Miquelon", "+508", R.drawable.flag_pm, "EUR"),
            Country("PN", "Pitcairn", "+872", R.drawable.flag_pn, "NZD"),
            Country("PR", "Puerto Rico", "+1", R.drawable.flag_pr, "USD"),
            Country("PS", "Palestinian Territory, Occupied", "+970", R.drawable.flag_ps, "ILS"),
            Country("PT", "Portugal", "+351", R.drawable.flag_pt, "EUR"),
            Country("PW", "Palau", "+680", R.drawable.flag_pw, "USD"),
            Country("PY", "Paraguay", "+595", R.drawable.flag_py, "PYG"),
            Country("QA", "Qatar", "+974", R.drawable.flag_qa, "QAR"),
            Country("RE", "Reunion", "+262", R.drawable.flag_re, "EUR"),
            Country("RO", "Romania", "+40", R.drawable.flag_ro, "RON"),
            Country("RS", "Serbia", "+381", R.drawable.flag_rs, "RSD"),
            Country("RU", "Russia", "+7", R.drawable.flag_ru, "RUB"),
            Country("RW", "Rwanda", "+250", R.drawable.flag_rw, "RWF"),
            Country("SA", "Saudi Arabia", "+966", R.drawable.flag_sa, "SAR"),
            Country("SB", "Solomon Islands", "+677", R.drawable.flag_sb, "SBD"),
            Country("SC", "Seychelles", "+248", R.drawable.flag_sc, "SCR"),
            Country("SD", "Sudan", "+249", R.drawable.flag_sd, "SDG"),
            Country("SE", "Sweden", "+46", R.drawable.flag_se, "SEK"),
            Country("SG", "Singapore", "+65", R.drawable.flag_sg, "SGD"),
            Country(
                "SH", "Saint Helena, Ascension and Tristan Da Cunha", "+290", R.drawable.flag_sh,
                "SHP"
            ),
            Country("SI", "Slovenia", "+386", R.drawable.flag_si, "EUR"),
            Country("SJ", "Svalbard and Jan Mayen", "+47", R.drawable.flag_sj, "NOK"),
            Country("SK", "Slovakia", "+421", R.drawable.flag_sk, "EUR"),
            Country("SL", "Sierra Leone", "+232", R.drawable.flag_sl, "SLL"),
            Country("SM", "San Marino", "+378", R.drawable.flag_sm, "EUR"),
            Country("SN", "Senegal", "+221", R.drawable.flag_sn, "XOF"),
            Country("SO", "Somalia", "+252", R.drawable.flag_so, "SOS"),
            Country("SR", "Suriname", "+597", R.drawable.flag_sr, "SRD"),
            Country("SS", "South Sudan", "+211", R.drawable.flag_ss, "SSP"),
            Country("ST", "Sao Tome and Principe", "+239", R.drawable.flag_st, "STD"),
            Country("SV", "El Salvador", "+503", R.drawable.flag_sv, "SVC"),
            Country("SX", "Sint Maarten", "+1", R.drawable.flag_sx, "ANG"),
            Country("SY", "Syrian Arab Republic", "+963", R.drawable.flag_sy, "SYP"),
            Country("SZ", "Swaziland", "+268", R.drawable.flag_sz, "SZL"),
            Country("TC", "Turks and Caicos Islands", "+1", R.drawable.flag_tc, "USD"),
            Country("TD", "Chad", "+235", R.drawable.flag_td, "XAF"),
            Country("TF", "French Southern Territories", "+262", R.drawable.flag_tf, "EUR"),
            Country("TG", "Togo", "+228", R.drawable.flag_tc, "XOF"),
            Country("TH", "Thailand", "+66", R.drawable.flag_th, "THB"),
            Country("TJ", "Tajikistan", "+992", R.drawable.flag_tj, "TJS"),
            Country("TK", "Tokelau", "+690", R.drawable.flag_tk, "NZD"),
            Country("TL", "East Timor", "+670", R.drawable.flag_tl, "USD"),
            Country("TM", "Turkmenistan", "+993", R.drawable.flag_tm, "TMT"),
            Country("TN", "Tunisia", "+216", R.drawable.flag_tn, "TND"),
            Country("TO", "Tonga", "+676", R.drawable.flag_to, "TOP"),
            Country("TR", "Turkey", "+90", R.drawable.flag_tr, "TRY"),
            Country("TT", "Trinidad and Tobago", "+1", R.drawable.flag_tt, "TTD"),
            Country("TV", "Tuvalu", "+688", R.drawable.flag_tv, "AUD"),
            Country("TW", "Taiwan", "+886", R.drawable.flag_tw, "TWD"),
            Country("TZ", "Tanzania, United Republic of", "+255", R.drawable.flag_tz, "TZS"),
            Country("UA", "Ukraine", "+380", R.drawable.flag_ua, "UAH"),
            Country("UG", "Uganda", "+256", R.drawable.flag_ug, "UGX"),
            Country("UM", "U.S. Minor Outlying Islands", "+1", R.drawable.flag_um, "USD"),
            Country("US", "United States", "+1", R.drawable.flag_us, "USD"),
            Country("UY", "Uruguay", "+598", R.drawable.flag_uy, "UYU"),
            Country("UZ", "Uzbekistan", "+998", R.drawable.flag_uz, "UZS"),
            Country("VA", "Holy See (Vatican City State)", "+379", R.drawable.flag_va, "EUR"),
            Country("VC", "Saint Vincent and the Grenadines", "+1", R.drawable.flag_cc, "XCD"),
            Country("VE", "Venezuela, Bolivarian Republic of", "+58", R.drawable.flag_ve, "VEF"),
            Country("VG", "Virgin Islands, British", "+1", R.drawable.flag_vg, "USD"),
            Country("VI", "Virgin Islands, U.S.", "+1", R.drawable.flag_vi, "USD"),
            Country("VN", "Vietnam", "+84", R.drawable.flag_vn, "VND"),
            Country("VU", "Vanuatu", "+678", R.drawable.flag_vu, "VUV"),
            Country("WF", "Wallis and Futuna", "+681", R.drawable.flag_wf, "XPF"),
            Country("WS", "Samoa", "+685", R.drawable.flag_ws, "WST"),
            Country("XK", "Kosovo", "+383", R.drawable.flag_xk, "EUR"),
            Country("YE", "Yemen", "+967", R.drawable.flag_ye, "YER"),
            Country("YT", "Mayotte", "+262", R.drawable.flag_yt, "EUR"),
            Country("ZA", "South Africa", "+27", R.drawable.flag_za, "ZAR"),
            Country("ZM", "Zambia", "+260", R.drawable.flag_zm, "ZMW"),
            Country("ZW", "Zimbabwe", "+263", R.drawable.flag_zw, "USD")
        )

        val countryList: List<Country>
            get() {
                val countries = Arrays.asList(*COUNTRIES)

                countries.sortWith(Comparator { o1: Country, o2: Country -> o1.name!!.compareTo(o2.name!!) })

                return countries
            }
    }
    // endregion
}