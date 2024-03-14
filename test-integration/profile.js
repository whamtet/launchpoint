const {assert} = require('chai');
const {clickButton, setInput, clickInput, typeInput, typeTextArea} = require('./util');

const getInnerText = (page, selector) => page.$eval(selector, el => el.innerText);

async function canEditName(page) {
    // test button edit
    await clickButton(page, 'Edit');
    await page.waitForNetworkIdle();

    await setInput(page,'first_name', 'Testy');
    await clickInput(page, 'Save');
    await page.waitForNetworkIdle();

    assert.equal(await getInnerText(page, '#names'), 'Testy User');
}

async function canEditDescription(page) {

    await page.type('textarea', 'Experienced worker.');
    await page.click('#description-title');
    await page.waitForNetworkIdle();
    await page.reload();

    const updatedDescription = await page.$eval('textarea', e => e.value);
    assert.equal(updatedDescription, 'Experienced worker.');

}

async function canAddJobs(page) {

    await clickButton(page, 'Add Job');
    await page.waitForNetworkIdle();
    await typeInput(page, 'title', 'Manager');
    await page.type('#company-search-box', 'Fine Organics');
    await typeInput(page, 'from-year', '2000');
    await typeInput(page, 'to-year', '2001');
    await typeTextArea(page, 'description', 'Made $$$!');
    await clickInput(page, 'Save');

}

async function profile(page) {

    await page.goto('http://localhost:3001/profile/');

    // await canEditName(page);
    // await canEditDescription(page);
    await canAddJobs(page);
}

exports.profile = profile;
