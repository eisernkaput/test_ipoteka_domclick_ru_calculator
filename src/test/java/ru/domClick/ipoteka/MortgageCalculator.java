package ru.domClick.ipoteka;

import java.util.Random;

public class MortgageCalculator {

    MortgageCalculator() {
    }

    //Расчет ежемесячного аннуитетного платежа, по сумме кредита, год. ставке и сроку кредита.
    private static double expectedMonthlyPaymentCalculator(int sumOfCredit, double yearRate, int creditTerm) {

        double percentPerMonth = (yearRate / 12);
        //Округление до ближайшего целого
        double iN = Math.pow(1 + percentPerMonth, creditTerm * 12);
        //Коэффициент аннуитета
        double K = (percentPerMonth * iN) / (iN - 1);
        double monthlyPayment = K * sumOfCredit;

        System.out.println(percentPerMonth + "\n"
                + iN + "\n"
                + K + "\n"
                + monthlyPayment + "\n");

        return monthlyPayment;

    }

    /* Генератор случайных условий кредита. Передает данные в калькулятор
            и в метод преобразования данных для запросов Selenium. Получает данные обратно в виде списка.
                 */
    public int[] expectedPerMonthRateCalculator() {

        // Генератор случ чисел
        Random rnd = new Random(System.currentTimeMillis());

        int[] requestData = new int[13];

        // Тип кредита
        int credType = rnd.nextInt(8);


        /* Процентная ставка по кредиту(годовая)
        Задается путем прибавления к 0 ставки по каждой скидке(если скидки нет то +),
        далее добавляется базовая ставка в зависимости от типа кредита
         */

        double yearRate = 0.00;

        switch (credType) {
            case 0:
                yearRate = 0.102;
                break;
            case 1:
                yearRate = 0.085;
                break;
            case 2:
                yearRate = 0.116;
                break;
            case 3:
                yearRate = 0.111;
                break;
            case 4:
                yearRate = 0.13;
                break;
            case 5:
                yearRate = 0.109;
                break;
            case 6:
                yearRate = 0.095;
                break;
            case 7:
                yearRate = 0.06;
                break;
            case 8:
                yearRate = 0.116;
                break;
        }

        //Общие параметры кредита
        int initialFee = 0;
        int realtyValue = 0;
        int sumOfCredit;


        //Скидка по наличию з/п карты
        boolean salaryCardDiscount = rnd.nextBoolean();
        //Скидка по наличию подтв дохода
        boolean incomeConfirmDiscount = true;
        if (!salaryCardDiscount) incomeConfirmDiscount = rnd.nextBoolean();
        //Возможность выдачи вредита
        boolean credPossible = true;
        //Скидка по страх. жизни
        boolean lifeInsuranceDiscount = true;
        //Скидка от домклик
        boolean domClickDiscount = true;
        //Скидка молодой семье
        boolean youngFamilyDiscount = true;
        //Скидка от застройщика
        boolean developerDiscount = true;
        //Скидка по первонач взносу
        boolean feeDiscount = true;
        //Скидка по электроннной регистрации
        boolean regDiscount = true;

        //----------------------------------Скидки общие-----------------------------------------------


        //Скидки  (кроме военн. ипотеки и с гос.поддержк.)
        if (credType <= 5 || credType == 8) {
            //Скидка по страх. жизни
            lifeInsuranceDiscount = rnd.nextBoolean();
            if (!lifeInsuranceDiscount) {
                yearRate += 0.01;
            }
        }
        //Скидка по наличию з/п карты  (кроме рефинанс)
        if (credType <= 4 || credType == 8) {
            if (!salaryCardDiscount) {
                if (credType == 4) yearRate += 0.005;
                yearRate += 0.003;
            }
        }

        //----------------------------------Общие условия кредитов-----------------------------------------------
        //Срок кредита
        int creditTerm = 1 + rnd.nextInt(30 - 1 + 1);
        //Срок кредита для нецелев. и военной ипотеки
        if (credType == 4 || credType == 6) creditTerm = 1 + rnd.nextInt(20 - 1 + 1);

        //----------------------------------Условия отдельных видов кредитов------------------------------------------

        //Условия по строит. дома, приобретению загород. дома и кр на машиноместо/гараж
        if (credType == 2 || credType == 3 || credType == 8) {
            //Стоимость жилья
            realtyValue = 400000 + rnd.nextInt(70000000 - 400000 + 1);
            //Первоначальный взнос
            initialFee = (int) (realtyValue * 0.25 + rnd.nextInt((int) ((realtyValue - 300000) - (realtyValue * 0.25) + 1)));
            //если нет подтв дохода- нет кредита(меняется на приобретение готового жилья)
            if (!salaryCardDiscount && !incomeConfirmDiscount) {
                credPossible = false;
                credType = 0;
            }
        }

        //---------------------------------------------------------------------------------------------

        //Условия по кред. на гот. жилье и новостройку
        if (credType == 0 || credType == 1) {
            //Стоимость жилья
            realtyValue = 353000 + rnd.nextInt(70000000 - 353000 + 1);
            //Первоначальный взнос
            initialFee = (int) (realtyValue * 0.15 + rnd.nextInt((int) ((realtyValue - 300000) - (realtyValue * 0.15) + 1)));
            //Первоначальный взнос менее 20% от стоимости
            if (initialFee <= realtyValue * 0.2) {
                feeDiscount = false;
                yearRate += 0.002;
            }
            //Скидка от домклик и для молодой семьи по гот.жилью
            if (credType == 0) {
                domClickDiscount = rnd.nextBoolean();
                if (!domClickDiscount) yearRate += 0.003;

                youngFamilyDiscount = rnd.nextBoolean();
                if (!youngFamilyDiscount) yearRate += 0.005;
            }
            //Скидка по подтв дохода, если нет з/п карты
            if (!salaryCardDiscount && !incomeConfirmDiscount) {
                yearRate += 0.003;
                initialFee = (int) (realtyValue * 0.5 + rnd.nextInt((int) ((realtyValue - 300000) - (realtyValue * 0.5) + 1)));
            }
            //Скидка по электроннной регистрации
            regDiscount = rnd.nextBoolean();
            if (!regDiscount) yearRate += 0.001;
            //Скидка застройщика (если новостройка+срок кредита менее 12 лет)
            if (credType == 1 && creditTerm <= 12) {
                developerDiscount = rnd.nextBoolean();
                if (!developerDiscount) yearRate += 0.015;
                //Скидка застройщика (если новостройка+срок кредита менее 7 лет)
                if (creditTerm >= 7) {
                    if (!developerDiscount) yearRate += 0.02;
                }
            }
        }

        //---------------------------------------------------------------------------------------------


        //Условия по военной ипотеке
        if (credType == 6) {
            //Стоимость жилья
            realtyValue = 353000 + rnd.nextInt(70000000 - 353000 + 1);
            //Первоначальный взнос
            initialFee = (int) (realtyValue * 0.15 + rnd.nextInt((int) ((realtyValue - 300000) - (realtyValue * 0.15) + 1)));
            if (realtyValue - initialFee > 2502000)
                initialFee += realtyValue - 2502000 - initialFee;

        }
        //---------------------------------------------------------------------------------------------
        //Условия по кр с гос поддержкой
        if (credType == 7) {
            //Стоимость жилья
            realtyValue = 375000 + rnd.nextInt(70000000 - 375000 + 1);
            //Первоначальный взнос
            initialFee = (int) (realtyValue * 0.2 + rnd.nextInt((int) ((realtyValue - 300000) - (realtyValue * 0.2) + 1)));
            if (realtyValue - initialFee > 12000000)
                initialFee += realtyValue - 12000000 - initialFee;
        }
        //---------------------------------------------------------------------------------------------

        //Сумма кредита
        sumOfCredit = realtyValue - initialFee;
        //---------------------------------------------------------------------------------------------
        //Условия по нецелевому кредиту
        if (credType == 4) {
            //Стоимость жилья
            realtyValue = 833334 + rnd.nextInt(70000000 - 833334 + 1);
            //сумма кредита
            sumOfCredit = 500000 + rnd.nextInt((int) (realtyValue * 0.6 - 500000 + 1));
            //если нет подтв дохода- нет кредита(меняется на приобретение готового жилья)
            requestData[5] = sumOfCredit;
            if (!salaryCardDiscount && !incomeConfirmDiscount) {
                credPossible = false;
                credType = 0;
            }
        }
        //---------------------------------------------------------------------------------------------

        //---------------------------------------------------------------------------------------------
        //Условия по рефинансированию
        if (credType == 5) {
            //Стоимость жилья
            realtyValue = 375000 + rnd.nextInt(70000000 - 375000 + 1);
            //остаток долга
            sumOfCredit = 300000 + rnd.nextInt((int) (realtyValue * 0.8 - 300000 + 1));
            if (realtyValue > 30000000) sumOfCredit = (int) (Math.floor(realtyValue / 10000) + 10) * 100 +
                    rnd.nextInt((int) (realtyValue * 0.8 - (int) (Math.floor(realtyValue / 10000) + 10) * 100 + 1));
            if (sumOfCredit > 7000000) sumOfCredit = 7000000;
        }
        //---------------------------------------------------------------------------------------------


        System.out.println(credType + " тип кредита \n" +
                realtyValue + " стоимость недвижимости \n" +
                initialFee + " первоначальный взнос \n" +
                sumOfCredit + " сумма кредита \n" +
                yearRate + " процентная ставка кредита \n" +
                creditTerm + " срок кредита \n" +
                credPossible + " возможность выдачи\n" +
                salaryCardDiscount + " з/п карта\n" +
                incomeConfirmDiscount + " подтв дохода\n" +
                lifeInsuranceDiscount + " страх жизни\n" +
                domClickDiscount + " домклик скидка\n" +
                youngFamilyDiscount + " молодая семья\n" +
                developerDiscount + " скидка застройщика\n" +
                feeDiscount + " взнос более 20%\n");

        // Ожидаемый ежемесячный платеж
        double expectedMonthlyPaymentDouble = expectedMonthlyPaymentCalculator(sumOfCredit, yearRate, creditTerm);
        int expectedMonthlyPayment;
        if (credType == 6) {
            expectedMonthlyPayment = (int) Math.round(expectedMonthlyPaymentDouble);
        } else {
            expectedMonthlyPayment = (int) Math.ceil(expectedMonthlyPaymentDouble);
        }

        requestData[0] = credType;
        requestData[1] = expectedMonthlyPayment;
        requestData[2] = realtyValue;
        requestData[3] = initialFee;
        requestData[4] = creditTerm;
        requestData[5] = sumOfCredit;

        requestData[6] = ((!salaryCardDiscount) ? 1 : 0);
        requestData[7] = ((!incomeConfirmDiscount) ? 1 : 0);
        requestData[8] = ((!lifeInsuranceDiscount) ? 1 : 0);
        requestData[9] = ((!domClickDiscount) ? 1 : 0);
        requestData[10] = ((!youngFamilyDiscount) ? 1 : 0);
        requestData[11] = ((!developerDiscount) ? 1 : 0);
        requestData[12] = ((!regDiscount) ? 1 : 0);

        return requestData;

    }


}
