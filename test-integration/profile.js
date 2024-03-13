const {assert} = require('chai');
const {clickValue} = require('./util');

const clickButton = async (page, text) => {
    const linkHandler = await page.$(`xpath/.//button[contains(text(), '${text}')]`);
    if (linkHandler) {
        linkHandler.click();
    }
};

const setInput = async (page, name, text) => {
    return page.$eval(`input[name="${name}"]`, (input, text) => input.value = text, text);
}

const getInnerText = (page, selector) => page.$eval(selector, el => el.innerText);

async function canEditName(page) {
    // test button edit
    await clickButton(page, 'Edit');
    await page.waitForNetworkIdle();

    await setInput(page,'first_name', 'Testy');
    await clickValue(page, 'Save');
    await page.waitForNetworkIdle();

    assert.equal(await getInnerText(page, '#names'), 'Testy User');
}

async function profile(page) {

    await page.goto('http://localhost:3001/profile/');

    await canEditName(page);
}

exports.profile = profile;
